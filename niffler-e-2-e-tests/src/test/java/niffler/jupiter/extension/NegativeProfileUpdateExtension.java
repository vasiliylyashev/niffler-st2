package niffler.jupiter.extension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NegativeProfileUpdateExtension extends BrowserExtension {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {
        if (throwable.getCause().toString().contains("ArgumentConversionException")) {
            Assertions.assertTrue(throwable.getMessage().contains("not one of the values accepted for Enum class: [EUR, KZT, USD, RUB]"));
        } else {
            throw throwable;
        }
    }
}
