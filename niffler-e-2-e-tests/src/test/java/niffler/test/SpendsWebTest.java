package niffler.test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import niffler.data.User;
import niffler.jupiter.GenerateCategory;
import niffler.jupiter.GenerateCategoryExtension;
import niffler.jupiter.GenerateSpend;
import niffler.jupiter.GenerateSpendExtension;
import niffler.model.CurrencyValues;
import niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(GenerateCategoryExtension.class)
@ExtendWith(GenerateSpendExtension.class)
public class SpendsWebTest {

    public static final User user = new User("TEST_USER", "12345");;

    static {
        Configuration.browserSize = "1920x1080";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();
    }

    @GenerateCategory(
        username = "TEST_USER",
        category = "Automation Testing Course QA.GURU"
    )
    @GenerateSpend(
        username = "TEST_USER",
        description = "QA GURU ADVANCED VOL 2",
        currency = CurrencyValues.RUB,
        amount = 54950.00,
        category = "Automation Testing Course QA.GURU"
    )
    @Test
    void spendShouldBeDeletedByActionInTable(SpendJson spend) {
        $(".spendings-table tbody").$$("tr")
            .find(text(spend.getDescription()))
            .$$("td").first()
            .scrollTo()
            .click();

        $$(".button_type_small").find(text("Delete selected"))
            .click();

        $(".spendings-table tbody")
            .$$("tr")
            .shouldHave(CollectionCondition.size(0));
    }
}
