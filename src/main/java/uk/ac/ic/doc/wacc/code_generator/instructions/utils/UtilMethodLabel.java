package uk.ac.ic.doc.wacc.code_generator.instructions.utils;

/* Enum for storing all branching labels when printing, reading
   or checking for a runtime error */
public enum UtilMethodLabel {
    p_print_int,
    p_print_string,
    p_print_bool,
    p_print_ln,

    /* List methods */
    p_print_list_int,
    p_print_list_bool,
    p_print_list_char,
    p_print_list_string,
    p_print_list_reference,
    p_remove_from_list,
    p_get_last_node,
    p_get_list_element,
    p_list_contains,
    p_check_list_bounds,
    p_free_list,

    p_read_int,
    p_read_char,
    p_print_reference,
    p_free_pair,
    p_free_array,
    p_check_divide_by_zero,
    p_check_array_bounds,
    p_check_null_pointer,
    p_throw_overflow_error,
    p_throw_runtime_error;
}
