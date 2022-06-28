package com.abdul.taskmaster.database;



import java.util.Date;

public class TaskMasterDatabaseConverter {
    
    public static Date fromTimeStamp (Long value) {
        return value == null ? null : new Date(value);
    }


    public static Long dateToTimeStamp (Date date) {
        return date == null ? null : date.getTime();
    }
}
