# Campus-Wrapper Documentation


This Android app, written in [Kotlin](https://developer.android.com/kotlin), displays content from the [Campus Page](https://www.notion.so/Campus-Wrapper-Documentation-9c48c15395824795b0eafe461a2aad6b) of the Alpen Adria University in Klagenfurt, including its lectures and exams. It includes a detailed view of a lecture that is based on the course page of the Universities site.

In order to display detailed lecture information, an external API must be utilized. This is because the Universities course page loads the lecture schedules dynamically, meaning that the inital html document does not contain the schedules-sessions of a lecture. While this may seem like an additional step, implementing this API, comes with further benefits, such as the option to display user-specific-content. This may include lectures or exams that a student is registered to or schedules meetings with the exam-administrations and more. Overall, while the use of an external API may add an extra layer of complexity to the process of accessing lecture information, it ultimately allows for a more detailed representation of a lectures content and more possibilties regarding additional features.

# Functionalities

The [Campus-Wrapper](https://github.com/Marty-Byrde/Campus-Wrapper) is capable of displaying lectures and exams, which have been retrieved from the Campus page of the University of Klagenfurt. Since there is no public api for this kind of data-retrieval, the required data is retrieved manually by scraping the universities campus-page with a library called [Jsoup](https://jsoup.org/).

## Jsoup

Jsoup is a library that can be used in an android application. It comes with functions such as `connect` and `parse` . With the help of those two functions the html content of a page can be fetched and parsed. Here is a quick example of how it can be done:

`val doc: Document = Jsoup.connect("https://en.wikipedia.org/").get();`

Running this code parses the fetched html content from [wikipedia](https://en.wikipedia.org/) to a Document object. This object comes with powerfull functions, which can be used to navigate through the document, just like the JSDOM, such as `getElementsByClass`, `getElementById` and many more.

As a result the lectures and exams of the campus page can be retrieved using Jsoup.  When it comes to retrieving detailes of a lecture, the corresponding course page has to be fetched and parsed. This works fine, except when trying to parse a lectures schedule, as those are not part of the inital html document. Instead the schedules are dynamically inserted into the course page after a short delay. Because of this, an external [API](https://github.com/Marty-Byrde/Campus-Wrapper-API) had to be implemented.

## Campus-Wrapper-API

This API is used to fetch the contents of a lecture’s course page, once its contents have loaded. As always, there are many libraries that can be uses in such a scenario. In this case, [Puppeteer](https://pptr.dev/) has been chosen. Puppeteer is a library that uses a Browser called chroium to scrape websites. In our case the API has been setup using those packages Typescript, Express, Puppeteer, just to name a few.

### How does the API operate?

Again, the API is used for the retrieval of detailed lecture, thus information about a lecture from its corresponding course page. Therefore, a simple Express server has been setup, which has been setup to run on port 80. Once the Server is started a new Browser instance is created and launched. By doing so all incomming requests can be in the same browser, which reduces the overall memory-usage, compared to launching a new browser for each request.

When a request is made to the following route `<API-Host/course?id=` with the corresponding lecture the id, then a new tab inside the Browser will be created. This page navigates to the course page of the lecture and waits for its contents to load. Thus, it waits for the lecture-schedules to load. Once those schedules are present the html document of the page will be retrieved and send as a response.

<aside>
📌 While the page has to wait for the lecture-schedule to appear, the images of the contributors are being retrieved as well. By default the image but also the email address and other information of a contributor are not included in the html content, as they are also dynamically added to the document, after clicking on a the name of contributor. Since Puppeteer can perform the actions as a regular user, it will click on all the contributor names, which opens a popup-menu that shows the image and other information, of each contributor. Once all the images are retrieved, a script tag is added to the html-document. It has its type set to ‘application/json’ and includes an array of type string with the url’s of each image. 

Once the images and the lecture-schedules have been loaded and retrieved the page will be closed and its html-document returned as a response.

</aside>

### Rate Limits

Since the app displays quite a few lectures and exams, depending on the lectures of a students-study a rate limit for the API and the App has been set. This has been achieved by including the [express-queue](https://www.npmjs.com/package/express-queue) package. Thorugh this package the amount of simultaneous requests, but also the total amount of requests, can be set. In our case the total amount of requests is set to be infinity, as our API should available for requests at all times.

The Rate-limit has been setup by executing this statement: `app.use(queue({ activeLimit: 10, queuedLimit: -1 }));` whereas, `app` is the express-object.

On the other hand, the rate-limit of our android-application has been setup using a [ThreadPoolExecutor](https://developer.android.com/reference/java/util/concurrent/ThreadPoolExecutor). This limits the amount of Threads that run at the same time, while adding Threads that would exceed the specified amount to a queue. Once a Thread is finished a new Thread of the queue will be started.

<aside>
📌 Keep in mind that the Android application will only communicate with the API, when fetching lecture-details. Thus, this rate-limit will only apply when fetching all the lecture-details at the same time. At the moment this happens in the background to increase the performance once the details are cached and stored.

</aside>

## Caching and Storage

To increase the applications performance the lecture-details are cached and stored once they have been fetched. By doing so the details are only fetched once. They are cached inside the App using a simple `StorageHandler` object. Additionally, they will be stored locally inside of a file.

When accessing the detailed-lectures inside the app the `StorageHandler` will be used to access them, as he will either return the cached or locally stored lectures.

<aside>
❔ Now what happens if the lectures-details are fetched and stored locally. Will they be fetched again, at some point?

</aside>

Of course the lecture-details are going to be fetched and stored frequently to ensure that they remain up-to-date. To be more specific, the app will always perform once fetch requests, once its started. If the fetch-request is successful its result will be cached and stored locally.