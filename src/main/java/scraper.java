import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class scraper {
    public static void main(String[] args) throws InterruptedException {
        boolean frontPageArticle = false;

        WebDriver driver = new FirefoxDriver();

        driver.get("https://news.web.nhk/newsweb/pl/news-nwa-latest-nationwide");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(),'I understand') or contains(text(),'確認しました')]")));

        wait.until(ExpectedConditions.visibilityOf(button));
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('div._13oezxo0, div._22j9o20').forEach(e => e.style.display='none');"
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

        Thread.sleep(3000);

        driver.get("https://news.web.nhk/newsweb/pl/news-nwa-latest-nationwide");

        System.out.println(driver.getTitle());

        WebElement element = driver.findElement(By.className("vmdvll5"));

//        System.out.println(element.getText());

        Predicate<String> sumoArticle = line -> line.contains("大相撲");

        List<WebElement> articles = element.findElements(By.className("vmdvll9"));

        for (WebElement article : articles) {
            try {
                String title = article.findElement(By.className("_158e1gz0")).getText();

                if (sumoArticle.test(title)) {
                    System.out.println(title + "\n");

                    String link = article.findElement(By.tagName("a")).getAttribute("href");

                    if (link != null) {
                        driver.get(link);
                        frontPageArticle = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (frontPageArticle) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(4))
                        .until(ExpectedConditions.presenceOfElementLocated(By.className("_1i1d7sh2")));

                List<WebElement> pTags = driver.findElements(By.className("_1i1d7sh2"));

                for (WebElement pTag : pTags) {
                    try {
                        System.out.println(pTag.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No sumo articles found on the front page. 大相撲の記事が見つからない");
        }

        driver.close();
    }
}
