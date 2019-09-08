package api;

import dao.Account;
import org.junit.jupiter.api.*;
import rest.v1.Handler;
import spark.Spark;
import storage.InMemoryStorage;
import storage.Storage;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;

public class ServiceApiV1Test {
    private static Storage storage = new InMemoryStorage();;
    private static Handler handler = new rest.v1.Handler(storage);
    private String serviceUri = "http://localhost:" + Spark.port() + "/api/v1/";
    private String accountsUri = serviceUri + "accounts";
    private String transferUri = serviceUri + "transfer";
    private Client client = ClientBuilder.newBuilder().build();

    private final Gson gson = new Gson();

    @BeforeAll
    static void setUpAll() {
        Spark.awaitInitialization();
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
    }

    @AfterAll
    static void tearDown() {
        Spark.stop();
    }

    @Test
    public void CreateAccount() {
        String payload = "{\"id\":\"test\", \"balance\":1000}";
        Response response = client.target(URI.create(accountsUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(200, response.getStatus());
        assertEquals("{\"status\":\"ok\",\"message\":{}}", response.readEntity(String.class));
        assertEquals( "/api/v1/accounts/test", response.getHeaders().getFirst("Location"));

        Account acc = storage.get("test");
        assertNotNull(acc);
        assertEquals(acc.getId(), "test");
        assertEquals(acc.getBalance(), 1000);
    }

    @Test
    public void GetAccountsEmpty() {
        Response response = client.target(URI.create(accountsUri))
                .request()
                .get();
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));
    }

    @Test
    public void GetAccounts() {
        storage.create(new Account("test1", 1000));
        storage.create(new Account("test2", 100));

        Response response = client.target(URI.create(accountsUri))
                .request()
                .get();
        assertEquals(200, response.getStatus());
        String expected = "[{\"id\":\"test1\",\"balance\":1000},{\"id\":\"test2\",\"balance\":100}]";
        assertEquals(expected, response.readEntity(String.class));
    }

    @Test
    public void Get() {
        storage.create(new Account("test", 1000));

        Response response = client.target(URI.create(accountsUri+"/test"))
                .request()
                .get();
        assertEquals(200, response.getStatus());
        String expected = "{\"id\":\"test\",\"balance\":1000}";
        assertEquals(expected, response.readEntity(String.class));
    }

    @Test
    public void GetNotFound() {
        Response response = client.target(URI.create(accountsUri+"/test"))
                .request()
                .get();
        assertEquals(404, response.getStatus());
        String expected = "{\"status\":\"error\",\"message\":{\"value\":\"Not found\"}}";
        assertEquals(expected, response.readEntity(String.class));
    }

    @Test
    public void Transfer() {
        storage.create(new Account("test1", 1000));
        storage.create(new Account("test2", 100));

        String payload = "{\"from\":\"test1\", \"to\":\"test2\", \"amount\":500}";
        Response response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(200, response.getStatus());
        assertEquals("{\"status\":\"ok\",\"message\":{}}", response.readEntity(String.class));

        Account acc1 = storage.get("test1");
        assertNotNull(acc1);
        assertEquals(500, acc1.getBalance());

        Account acc2 = storage.get("test2");
        assertNotNull(acc2);
        assertEquals(600, acc2.getBalance());
    }

    @Test
    public void TransferWithIdempotencyKeys() {
        storage.create(new Account("test1", 1000));
        storage.create(new Account("test2", 100));

        String payload = "{\"from\":\"test1\", \"to\":\"test2\", \"amount\":500, \"idempotencyKey\":\"key\"}";
        Response response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(200, response.getStatus());
        assertEquals("{\"status\":\"ok\",\"message\":{}}", response.readEntity(String.class));

        response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(200, response.getStatus());

        Account acc1 = storage.get("test1");
        assertNotNull(acc1);
        assertEquals(500, acc1.getBalance());

        Account acc2 = storage.get("test2");
        assertNotNull(acc2);
        assertEquals(600, acc2.getBalance());
    }

    @Test
    public void TransferErrors() {
        storage.create(new Account("test1", 1000));
        storage.create(new Account("test2", 100));

        // equal ids
        String payload = "{\"from\":\"test1\", \"to\":\"test1\", \"amount\":500, \"idempotencyKey\":\"key\"}";
        Response response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(400, response.getStatus());

        // insufficient amount
        payload = "{\"from\":\"test1\", \"to\":\"test2\", \"amount\":50000, \"idempotencyKey\":\"key\"}";
        response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(400, response.getStatus());

        // negative amount
        payload = "{\"from\":\"test1\", \"to\":\"test2\", \"amount\":-100, \"idempotencyKey\":\"key\"}";
        response = client.target(URI.create(transferUri))
                .request()
                .post(Entity.json(payload));
        assertEquals(400, response.getStatus());

        Account acc1 = storage.get("test1");
        assertNotNull(acc1);
        assertEquals(1000, acc1.getBalance());

        Account acc2 = storage.get("test2");
        assertNotNull(acc2);
        assertEquals(100, acc2.getBalance());
    }
}