package edu.byu.cs329.rd;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.byu.cs329.rd.ReachingDefinitions.Definition;

public class MockitoTest {
  
  @Mock
  ReachingDefinitions rd;
  
  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void mockTest() {
    Assertions.assertEquals(new HashSet<Definition>(), rd.getReachingDefinitions(null));
  }
}
