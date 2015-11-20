import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by kamilzie on 13/11/15.
 */
public class AppiumExampleTests {

    protected static IOSDriver<IOSElement> driver;

    @BeforeClass
    public static void setup() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        File appDir = new File(System.getProperty("user.dir"), "/app");

//      This run app in iPhone 5 (with iOS 9.1) simulator

        File app = new File(appDir, "ProductContainer_symulator_iPhone5_9_1.app");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.1");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 5");

//        Uncomment this section when you would like to run test on real device (here iPad Air 2 with iOS 9.1 is needed

//        File app = new File(appDir, "ProductContainer_device_iPadAir2_9.1.app");
//        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.1");
//        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPad Air 2");
//        capabilities.setCapability(MobileCapabilityType.UDID,"Type Here Your UDID");

        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @AfterClass
    public static void cleanUp(){
        driver.quit();
    }

    @Test
    public void addProductWithoutName_ShouldShowWaringAlert() {
        // find and click on 'Add Product' button
        driver.findElementByName("Add Product").click();

        // find and click on 'Add' button
        driver.findElementByName("Add").click();

        // find alert element
        IOSElement alert = driver.findElementByClassName("UIAAlert");

        // on alert element, find and click 'OK' element
        alert.findElementByName("OK").click();

        // other way to dismiss Alert
        // driver.switchTo().alert().accept();

        Assert.assertFalse(alert.isDisplayed());

        // go to main Menu
        driver.findElementByName("Back").click();
    }

    @Test
    public void example(){

        driver.findElementByName("Products").click();

        driver.findElementByName("Back").click();

    }

    @Test
    public void addValidProduct_ShouldNotShowAnyWarning() {
        IOSElement AddProductButton = driver.findElementByName("Add Product");
        AddProductButton.click();

        // find TextField and type there 'Apple'
        driver.findElementByClassName("UIATextField").sendKeys("Apple");

        IOSElement incrementButton = driver.findElementByName("Increment");
        incrementButton.click();
        incrementButton.click();
        incrementButton.click();

        // find switch and click it
        driver.findElementByXPath("//UIAApplication[1]/UIAWindow[1]/UIASwitch[1]").click();

        // find and set slider value
        driver.findElementByClassName("UIASlider").sendKeys("0.5"); //!

        driver.findElementByName("Add").click();

        // if product is added, main menu should appears
        Assert.assertTrue(AddProductButton.isDisplayed());
    }

    @Test
    public void HybridExample() throws InterruptedException {
        IOSElement productsButton = driver.findElementByName("Products");
        productsButton.click();

        IOSElement tableRows = driver.findElementByClassName("UIATableView");
        MobileElement foundElement = findTextElementContainsStringInTable(tableRows,"Apple");

        foundElement.click();

        // iterate through available contexts
        Set<String> contextNames = driver.getContextHandles();
        for (String contextName : contextNames) {
            System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
        }
        String contextNamesAsString = (String)contextNames.toArray()[1];

        driver.context(contextNamesAsString); // set context to WEBVIEW_1

        // google search box has this class
        String searchBoxClassName = "gsfi";

        // we can use features from normal selenium
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(searchBoxClassName)));

        WebElement searchBox =  driver.findElement(By.className(searchBoxClassName));
        Thread.sleep(3000);
        searchBox.sendKeys(" Photos");
        Thread.sleep(3000);
        searchBox.submit();
        Thread.sleep(3000);
        // switch co native app context
        driver.context("NATIVE_APP");

        // go to Products page
        driver.findElementByName("Back").click();

        // go to main Menu
        driver.findElementByName("Back").click();

        Assert.assertTrue(productsButton.isDisplayed());
    }

    @Test
    public void CheckIfDefaultProductExists_ShouldAppleProductBePresent() {
        driver.findElementByName("Products").click();

        IOSElement tableRows = driver.findElementByClassName("UIATableView");
        MobileElement foundElement = findTextElementContainsStringInTable(tableRows,"Apple");

        Assert.assertTrue(foundElement.isDisplayed());

        // go to main Menu
        driver.findElementByName("Back").click();
    }

    // helper finds every UIAStaticText in given element and check if contains phrase
    public MobileElement findTextElementContainsStringInTable(IOSElement table, String phrase)
    {
        List<MobileElement> labelsInCell = table.findElementsByClassName("UIAStaticText");
        for(MobileElement label : labelsInCell){
            if(label.getText().contains(phrase)){
                return  label;
            }
        }
        return null;
    }
}
