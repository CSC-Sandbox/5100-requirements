package edu.calpoly.message;

import java.beans.BeanProperty;
import java.time.LocalTime;

@FunctionalInterface
public interface Formatter {
    String format(String message);

    static String formatWithTimeStamp(String message){
        var time = LocalTime.now();

        return String.format("[%tT] %s %n", time, message);
    }
}
