package edu.byu.cs329.rd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

public class MockitoTest {
  
  @Mock
  ReachingDefinitions rd;
  
  @BeforeEach
  void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  void mockTest() {
    Assertions.assertNull(rd.getReachingDefinitions(null));
    boolean b = true;
    if (b) {
      b = false;
      ;
    };
  }
}
