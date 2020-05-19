package uk.ac.ic.doc.wacc.semantic_analyser;

import java.util.*;

public class FunctionSymbolTable<T> {
    /* Stores a function name with it's corresponding various function entries
       to deal with overloading */
    private final Map<String, List<T>> functionMap;

    public FunctionSymbolTable() {
        this.functionMap = new HashMap<>();
    }

    public List<T> getFuncListWithName(String name) {
        return functionMap.get(name);
    }

    public boolean exists(String name, T functionEntry) {
        if(!(functionMap.containsKey(name))) {
            return false;
        }

        List<T> entries = getFuncListWithName(name);
        return entries.contains(functionEntry);
    }

    /* Attempts to add a function entry to the correct list in the hashmap,
       if the function entry doesn't already exist. */
    public boolean add(String name, T functionEntry) {
        List<T> entries = getFuncListWithName(name);

        if(entries == null) {
            List<T> functionEntries = new ArrayList<>();
            functionEntries.add(functionEntry);
            functionMap.put(name, functionEntries);
            return true;
        }

        if (!entries.contains(functionEntry)) {
            entries.add(functionEntry);
            functionMap.put(name, entries);
            return true;
        }

        return false;
    }

    /* Checks if there exists a function with the given name and
       an equivalent function entry. */
    public Optional<T> retrieveEntry(String name, T functionEntry) {
        List<T> entries = getFuncListWithName(name);

        if(entries == null) {
            return Optional.empty();
        }

        for (T entry : entries) {
            if (functionEntry.equals(entry)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

}
