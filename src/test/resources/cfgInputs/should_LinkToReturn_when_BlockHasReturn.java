package cfgInputs;

public class should_LinkToReturn_when_BlockHasReturn {
  boolean name() {
    int i = 1;
    return i == 1;
    i = 2*i;
  }
}
