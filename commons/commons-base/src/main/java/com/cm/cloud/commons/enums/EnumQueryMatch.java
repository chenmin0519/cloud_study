package com.cm.cloud.commons.enums;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 */
@AllArgsConstructor
@Getter
public enum EnumQueryMatch {

    EQ("="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("like");

    private String value;

    public static EnumQueryMatch toEnum(String value) {

        return Stream.of(values()).filter(va -> va.getValue().equals(value))
                .findAny().orElseThrow(() -> new ParameterNotValidException("匹配方式不存在:" + value));
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
