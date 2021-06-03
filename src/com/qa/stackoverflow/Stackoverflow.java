package com.qa.stackoverflow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Stackoverflow {

	static String URL = "https://stackoverflow.com/";

	public static void main(String[] args) {

		WebDriverManager.chromedriver().setup();

		ChromeOptions option = new ChromeOptions();
		option.addArguments("--incognito");
		WebDriver driver = new ChromeDriver(option);

		driver.manage().window().maximize();

		driver.manage().timeouts().implicitlyWait(10L, TimeUnit.SECONDS);

		driver.get(URL);

		waitForPageToLoad(driver);
		Actions a = new Actions(driver);

		if (driver.findElements(By.xpath("//button[contains(text(),'Accept all cookies')]")).size() > 0) {
			driver.findElement(By.xpath("//button[contains(text(),'Accept all cookies')]")).click();
			System.out.println("Accepted the cookies");
		}

		// driver.quit();

		sendKeys(driver, By.name("q"), "[selenium]");

		driver.findElement(By.name("q")).sendKeys(Keys.ENTER);

		if (driver.findElements(By.xpath("//button[contains(text(),'Accept all cookies')]")).size() > 0) {
			driver.findElement(By.xpath("//button[contains(text(),'Accept all cookies')]")).click();
		}

		if (driver.findElements(By.xpath("//iframe[@title='reCAPTCHA']")).size() > 0) {
			new WebDriverWait(driver, 4).until(
					ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@title='reCAPTCHA']")));
		}

		if (driver.findElements(By.xpath("//span[@id='recaptcha-anchor']")).size() > 0) {
			System.out.println("Accepting I am not robot");

			a.moveToElement(driver.findElement(By.xpath("//span[@id='recaptcha-anchor']"))).click().build().perform();

		}

		driver.switchTo().defaultContent();

		// verify 15 links are displayed

		int size = driver.findElements(By.xpath("//div[@id='questions']//h3/a")).size();

		Assert.assertEquals(15, size);

		clickOnElement(driver, By.xpath("//div[@class='uql-nav grid--cell']/child::div/child::div[3]"));

		clickOnElement(driver, By.xpath("//input[@name='sortId' and @value='MostVotes']"));

		clickOnElement(driver, By.xpath("//button[contains(text(),'Apply filter')]"));

		clickOnElement(driver, By.xpath("//button[contains(text(),'Cancel')]"));

		List<WebElement> title = driver.findElements(By.xpath("//div[@class='summary']/h3/a"));
		ArrayList<String> listOfTitle = new ArrayList<String>();

		for (WebElement e : title) {
			listOfTitle.add(e.getText());
		}

		List<WebElement> votes = driver.findElements(By.xpath("//div[@class='stats']//div[@class='votes']//strong"));

		ArrayList<String> numberOfVotes = new ArrayList<String>();

		for (WebElement e : votes) {
			numberOfVotes.add(e.getText());
		}

		List<WebElement> ansCount = driver.findElements(By.xpath("//div[@class='stats']//div[2]/strong"));

		ArrayList<String> coutOfAns = new ArrayList<String>();

		for (WebElement e : ansCount) {
			coutOfAns.add(e.getText());
		}

		for (int i = 0; i < 15; i++) {

			System.out.println("Tile is: " + listOfTitle.get(i) + " with votes: " + numberOfVotes.get(i)
					+ " and answer count: " + coutOfAns.get(i));

		}

		// get other tags

		// acceptedand = //div[@itemprop='acceptedAnswer']//div[@itemprop='upvoteCount']

		List<WebElement> clickOnEachLink = driver.findElements(By.xpath("//div[@class='summary']/h3/a"));

		ArrayList<String> list = new ArrayList<String>();

		// Each link is stored and we are clicking the link to open in new tab and get
		// the details.

		for (WebElement e : clickOnEachLink) {
			scrollIntoView(driver, e);
			String content = e.getText();
			String selectLinkOpeninNewTab = Keys.chord(Keys.COMMAND, Keys.RETURN);
			e.sendKeys(selectLinkOpeninNewTab);

			ArrayList<String> li = new ArrayList<String>(driver.getWindowHandles());

			driver.switchTo().window(li.get(1));
			waitForPageToLoad(driver);
			try {
				WebElement element = driver
						.findElement(By.xpath("//div[@itemprop='acceptedAnswer']//div[@itemprop='upvoteCount']"));
				scrollIntoView(driver, element);
				list.add("For link: " +content +" ; Accepted answer is == "+element.getText());
			} catch (Exception e1) {
				System.out.println(content + " -- does not have accepted answers");
			}

			driver.close();
			driver.switchTo().window(li.get(0));
			li.clear();

		}

		// This will print the accepted answers in each link

		for (String s : list) {
			System.out.println( s);
		}

	

	}

	public static void waitForPageToLoad(WebDriver driver) {

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, 5L);

		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				// TODO Auto-generated method stub
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		wait.until(condition);

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

	}

	public static void clickOnElement(WebDriver driver, By by) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(by)).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	public static void sendKeys(WebDriver driver, By by, String values) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(by)).sendKeys(values);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	public static void scrollIntoView(WebDriver driver, WebElement e) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", e);
	}

}
