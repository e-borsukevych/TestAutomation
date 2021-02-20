package avic;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class AvicTests {
    private WebDriver driver;

    @BeforeTest
    public void wakeUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test(priority = 1)
    public void checkThatTvFilterShowsProductsOfSelectedManufacturer() {
        driver.findElement(By.xpath("//div[@class='top-links__left flex-wrap']//a[@href='/televizoryi']")).click();
        driver.findElement(By.xpath("//label[@for='fltr-proizvoditel-samsung']")).click();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@for='fltr-proizvoditel-samsung']" +
                "//following-sibling::a/span[contains(text(),'Показать')]"))).click();
        List<WebElement> filteredList = driver.findElements(By.xpath("//div[@class='prod-cart__descr']"));
        for (WebElement webElement : filteredList) {
            Assert.assertTrue(webElement.getText().contains("Samsung"));
        }
    }

    @Test(priority = 2)
    public void checkThatBenefitCalculatedCorrectly() {
        driver.findElement(By.xpath("//div[@class='top-links__left flex-wrap']//a[@href='/discount']")).click();
        int fullPrice = Integer.parseInt((driver.findElement(By.xpath("//div[@data-product=213723]" +
                "//div[@class='prod-cart__prise-old']")).getText()).replaceAll("[^0-9]", ""));
        int salePrice = Integer.parseInt((driver.findElement(By.xpath("//div[@data-product=213723]" +
                "//div[@class='prod-cart__prise-new']")).getText()).replaceAll("[^0-9]", ""));
        int Benefit = Integer.parseInt((driver.findElement(By.xpath("//div[@data-product=213723]" +
                "//div[@class='prod-cart__status-box bg-orange']")).getText()).replaceAll("[^0-9]", ""));
        Assert.assertEquals(fullPrice - salePrice, Benefit);
    }

    @Test(priority = 3)
    public void checkThatPriceLessThanFilterShowsCorrectResult() throws InterruptedException {
        driver.findElement(By.xpath("//span[@class='sidebar-item']")).click();
        Actions hoverTheElement = new Actions(driver);
        WebElement gajets = driver.findElement(By.xpath("//span[text() = 'Гаджеты']"));
        hoverTheElement.moveToElement(gajets).build().perform();

        //try to use Explicit Waits, test fail 1/5
//        WebDriverWait wait = new WebDriverWait(driver, 5);
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Гаджеты']" +
//                "//following::a[text()='Квадрокоптеры']"))).click();

        //try to use Implicit Waits, test fail 1/5
//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        driver.findElement(By.xpath("//span[text()='Гаджеты']//following::a[text()='Квадрокоптеры']")).click();

        //so I decide to use Thread.sleep, test fail 0/5
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[text()='Гаджеты']//following::a[text()='Квадрокоптеры']")).click();
        WebElement maxPrice = driver.findElement(By.xpath("//input[@class='form-control form-control-max']"));
        maxPrice.clear();
        maxPrice.sendKeys("15000", Keys.ENTER);
        WebDriverWait wait2 = new WebDriverWait(driver, 5);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='form-group filter-group " +
                "js_filter_parent open-filter-tooltip']//following-sibling::a/span[contains(text(),'Показать')]"))).click();
        List<WebElement> filteredList = driver.findElements(By.xpath("//div[@class='prod-cart__prise-new']"));
        for (WebElement webElement : filteredList) {
            Assert.assertFalse(Integer.parseInt((webElement.getText().replaceAll("[^0-9]", ""))) > 15000);
        }
    }

    @Test(priority = 4)
    public void checkIncorrectEmailWarningMessageIsShown() {
        driver.findElement(By.xpath("//div[@class='header-bottom__login flex-wrap middle-xs']/div[@class='header-bottom__right-icon']")).click();
        WebElement loginField = driver.findElement(By.xpath("//div[@class='sign-holder clearfix']//input[@class='validate']"));
        loginField.clear();
        loginField.sendKeys("fsdgsdfhgdfg", Keys.ENTER);
        String warningMessage = (driver.findElement(By.xpath("//span[text()='Пароль']//preceding::div[@class=" +
                "'form-field input-field flex error']"))).getAttribute("data-error");
        Assert.assertEquals(warningMessage, "Обязательное поле");
    }

    @AfterMethod
    public void goSleep() {
        driver.close();
    }
}
