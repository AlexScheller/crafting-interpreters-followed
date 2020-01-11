import os
import sys
from pathlib import Path

def output_ast(definitions, base_classname, directory):
	# Generate directory
	dir_path = Path(directory)
	dir_path.mkdir(exist_ok=True)
	# Generate main class
	with open(os.path.join(dir_path, f'{base_classname}.java'), 'w') as ast_file:
		ast_file.write('package lox;\n\n')
		ast_file.write('import java.util.List;\n\n')
		ast_file.write(f'abstract class {base_classname} {{\n\n')
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
			file.write('	}\n\n')
		for definition in definitions:
			write_definition(ast_file, definition)
		# close the out class
		ast_file.write('}')

def main(args):
	if len(args) != 1:
		print("Usage: generate_ast <output_directory>", file=sys.stderr)
		sys.exit(1)
	definitions = [
		{
			'classname': 'Binary',
			'fields': [
				{
					'name': 'left',
					'type': 'Expression'
				},
				{
					'name': 'operator',
					'type': 'Token'
				},
				{
					'name': 'right',
					'type': 'Expression'
				},
			]
		}
	]
	output_ast(definitions, 'Expression', args[0])

if __name__ == '__main__':
	main(sys.argv[1:])