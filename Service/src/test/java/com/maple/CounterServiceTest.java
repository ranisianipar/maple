package com.maple;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {CounterService.class})
public class CounterServiceTest {

    @InjectMocks
    CounterService counterService;

    @Mock
    IdCounterRepository counterRepository;

    IdCounter counter = new IdCounter();

    @Before
    public void init() {
        when(counterRepository.findFirst()).thenReturn(counter);
    }

    @Test
    public void getNextEmployeeTest() {
        assertEquals("EMP-0", counterService.getNextEmployee());
    }

    @Test
    public void getNextItemTest() {
        assertEquals("ITM-0", counterService.getNextItem());
    }

    @Test
    public void getNextAssignment() {
        assertEquals("ASG-0", counterService.getNextAssignment());
    }
}
