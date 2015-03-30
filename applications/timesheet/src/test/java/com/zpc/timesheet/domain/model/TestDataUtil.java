package com.zpc.timesheet.domain.model;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/**
 * Created by Administrator on 9/25/2014.
 */
public final class TestDataUtil {

    public static final AttendanceRegister getDummyAttendanceRegister(){
        return fillDataIntoObjectGraph(AttendanceRegister.class);
    }


    public static <T> T fillDataIntoObjectGraph(Class<T> clazz){
        PodamFactory factory = new PodamFactoryImpl(); //This will use the default Random Data Provider Strategy
        T objectGraph = factory.manufacturePojo(clazz);
        return objectGraph;
    }

    @Test
    public void emptyTest(){
        assertThat(true, is(true));
    }
}