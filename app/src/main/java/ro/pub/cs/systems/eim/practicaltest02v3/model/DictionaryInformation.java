package ro.pub.cs.systems.eim.practicaltest02v3.model;


import androidx.annotation.NonNull;

import java.util.List;

public class DictionaryInformation {
    private final String definition;

    public DictionaryInformation(String definition) {
        this.definition = definition;
    }

    public String getDefinitions() {
        return definition;
    }

    @NonNull
    @Override
    public String toString() {
            return "Definitions: " + definition;
    }
}

