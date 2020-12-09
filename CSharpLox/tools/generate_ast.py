import os
import sys
import json
from pathlib import Path

def output_ast(definitions, base_classname, directory=''):
	# Generate directory
	dir_path = Path(directory)
	dir_path.mkdir(exist_ok=True)
	# Generate main class
	with open(os.path.join(dir_path, f'{base_classname}.cs'), 'w') as ast_file:
		nesting = 1
		indent = '\t'
		# Preamble
		ast_file.write('using System;\n\n');
		ast_file.write('namespace CSharpLox\n')
		ast_file.write('{\n')
		ast_file.write(f'{indent * nesting}public abstract class {base_classname}\n')
		ast_file.write(f'{indent * nesting}{{\n')
		nesting += 1
		# Visitor
		ast_file.write(f'{indent * nesting}public interface IExpressionVisitor<T>\n')
		ast_file.write(f'{indent * nesting}{{\n')
		for definition in definitions:
			typ = definition['classname']
			ast_file.write(f'{indent * (nesting + 1)}public T Visit{typ}{base_classname}({typ} {base_classname.lower()});\n')
		ast_file.write(f'{indent * nesting}}}\n\n')

		# Visitor Pattern (abstract method in super class)
		ast_file.write(f'{indent * nesting}public abstract T Accept<T>(IExpressionVisitor<T> visitor);\n\n');

		def write_definition(file, nesting, definition):
			file.write(f'{indent * nesting}class {definition["classname"]} : {base_classname}\n')
			file.write(f'{indent * nesting}{{\n')
			# fields
			for field in definition['fields']:
				file.write(f'{indent * (nesting + 1)}readonly {field["type"]} {field["name"]};\n')
			# constructor
			arglist = ', '.join([f'{field["type"]} {field["name"]}' for field in definition['fields']])
			file.write(f'\n{indent * (nesting + 1)}{definition["classname"]} ({arglist}) {{\n')
			# field arguments
			for field in definition['fields']:
				file.write(f'{indent * (nesting + 2)}this.{field["name"]} = {field["name"]};\n')
			file.write(f'{indent * (nesting + 1)}}}\n')

			# Visitor Pattern (class implementation)
			file.write(f'{indent * (nesting + 1)}public override T Accept<T>(IExpressionVisitor<T> visitor) {{\n')
			file.write(f'{indent * (nesting + 2)}return visitor.Visit{definition["classname"]}{base_classname}(this);\n')
			file.write(f'{indent * (nesting + 1)}}}\n')
			# close the class
			file.write(f'{indent * nesting}}}\n\n')

		for definition in definitions:
			write_definition(ast_file, nesting, definition)
		# close the out class
		ast_file.write(f'{indent}}}\n');
		ast_file.write('}')

def main(args):
	if len(args) != 2:
		print("Usage: generate_ast <defintions_file> <output_directory>", file=sys.stderr)
		sys.exit(1)
	definitions = []
	with open(args[0], 'r') as definitions_file:
		definitions = json.load(definitions_file)
	output_ast(definitions, 'Expression', args[1])

if __name__ == '__main__':
	main(sys.argv[1:])