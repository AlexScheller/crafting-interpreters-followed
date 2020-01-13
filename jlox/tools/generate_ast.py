import os
import sys
import json
from pathlib import Path

def output_ast(definitions, base_classname, directory):
	# Generate directory
	dir_path = Path(directory)
	dir_path.mkdir(exist_ok=True)
	# Generate main class
	with open(os.path.join(dir_path, f'{base_classname}.java'), 'w') as ast_file:
		# Preamble
		ast_file.write('package lox;\n\n')
		ast_file.write('import java.util.List;\n\n')
		ast_file.write(f'abstract class {base_classname} {{\n\n')
		# Visitor
		ast_file.write('	interface Visitor<R> {\n\n')
		for definition in definitions:
			typ = definition['classname']
			ast_file.write(f'		R visit{typ}{base_classname}({typ} {base_classname.lower()});\n')
		ast_file.write('\n	}\n\n')

		ast_file.write('	abstract <R> R accept(Visitor<R> visitor);\n\n');

		def write_definition(file, definition):
			file.write(f'	static class {definition["classname"]} extends {base_classname} {{\n\n')
			# fields
			for field in definition['fields']:
				file.write(f'		final {field["type"]} {field["name"]};\n')
			# constructor
			arglist = ', '.join([f'{field["type"]} {field["name"]}' for field in definition['fields']])
			file.write(f'\n		{base_classname} ({arglist}) {{\n')
			# field arguments
			for field in definition['fields']:
				file.write(f'			this.{field["name"]} = {field["name"]};\n')
			# close the class
			file.write('		}\n\n')
			# Visitor Pattern
			file.write('		<R> R accept(Visitor<R> visitor) {\n')
			file.write(f'			return visitor.visit{definition["classname"]}{base_classname}(this);\n')
			file.write('		}\n\n')
			file.write('	}\n\n')
		for definition in definitions:
			write_definition(ast_file, definition)
		# close the out class
		ast_file.write('}')

def main(args):
	if 0 >= len(args) > 2:
		print("Usage: generate_ast <defintions_file> <output_directory>", file=sys.stderr)
		sys.exit(1)
	definitions = []
	with open(args[0], 'r') as definitions_file:
		definitions = json.load(definitions_file)
	output_ast(definitions, 'Expression', args[1])

if __name__ == '__main__':
	main(sys.argv[1:])