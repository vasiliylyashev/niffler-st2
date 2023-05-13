package niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class WrongCurrencyExtension implements TestExecutionExceptionHandler {

  private final String wrongCurrencyErrorMessage = "not one of the values accepted for Enum class: [EUR, KZT, USD, RUB]";
  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    if(null != throwable.getCause()){
      if(throwable.getCause().getClass().getName().contains("ArgumentConversionException")){
        Allure.step("Catching deserialization exception from API", ()-> {
          Assertions.assertTrue(throwable.getMessage().contains(wrongCurrencyErrorMessage),
                  "Incorrect message from API. Actual: " + throwable.getMessage() +
                          ". Expected: " + wrongCurrencyErrorMessage);
        });
        return;
      }
      throw throwable;
    }
  }
}