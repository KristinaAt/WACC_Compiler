ANTLR_DIR   := antlr_config
SOURCE_DIR  := src/main/java

# Tools

GRADLE  := ./gradlew
ANTLR   := antlrBuild
RM      := rm -rf

GRADLE_BUILD_ARGS := -x test

# the make rules

all: rules java

# runs the antlr build script
rules:
	cd $(ANTLR_DIR) && ./$(ANTLR)

# attempts to compile all non-test .java files
java:
	$(GRADLE) build $(GRADLE_BUILD_ARGS)

tests: lexer_tests parser_tests semantic_tests compiler_errors_tests symbol_table_tests printer_tests expression_type_visitor_tests code_generation_tests

# runs lexer tests
lexer_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.LexerTests

# runs parser tests
parser_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.ValidParserTests
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.InvalidParserTests

# runs invalid semantic tests
semantic_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.InvalidSemanticTests
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.ValidSemanticTests

# runs compiler errors tests
compiler_errors_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.CompileErrorTest

# runs symbol table tests
symbol_table_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.SymbolTableTest

# runs printer tests
printer_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.ASTPrinterTests

# runs expression type visitor tests
expression_type_visitor_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.GetExpressionTypeVisitorTest

# runs code generation tests
code_generation_tests:
	$(GRADLE) test --tests uk.ac.ic.doc.wacc.CodeGenerationTests

clean:
	$(GRADLE) clean
	$(RM) $(SOURCE_DIR)/uk/ac/ic/doc/wacc/antlr

.PHONY: all rules java tests lexer_tests parser_tests semantic_tests compiler_errors_tests symbol_table_tests printer_tests expression_type_visitor_tests code_generation_tests clean
