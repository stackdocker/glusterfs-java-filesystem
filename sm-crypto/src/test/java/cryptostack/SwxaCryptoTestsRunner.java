package cryptostack;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SwxaCryptoTestsRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(SwxaCryptoTests.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}