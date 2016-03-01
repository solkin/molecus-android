package com.tomclaw.molecus.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by solkin on 01/03/16.
 */
public class ExcludeFieldsStrategy implements ExclusionStrategy {

    public ExcludeFieldsStrategy() {
    }

    // This method is called for all fields. if the method returns false the
    // field is excluded from serialization
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ExcludeField.class) != null;
    }

    // This method is called for all classes. If the method returns false the
    // class is excluded.
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
