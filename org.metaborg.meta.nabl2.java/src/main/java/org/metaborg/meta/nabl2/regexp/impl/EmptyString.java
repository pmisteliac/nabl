package org.metaborg.meta.nabl2.regexp.impl;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.meta.nabl2.regexp.IRegExp;
import org.metaborg.meta.nabl2.regexp.IRegExpBuilder;

@Value.Immutable
@Serial.Version(value = 42L)
abstract class EmptyString<S> implements IRegExp<S> {

    @Value.Parameter public abstract IRegExpBuilder<S> getBuilder();

    @Override public boolean isNullable() {
        return true;
    }

    @Override public <T> T match(IRegExp.ICases<S, T> visitor) {
        return visitor.emptyString();
    }

    @Override public String toString() {
        return "e";
    }

}
