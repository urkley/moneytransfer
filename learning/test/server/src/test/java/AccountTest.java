import com.google.gson.Gson;
import com.revoluttask.api.AccountAPI;
import com.revoluttask.models.Account;
import com.revoluttask.models.Transaction;
import com.revoluttask.services.AccountService;
import com.revoluttask.services.AccountServiceImpl;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class AccountTest extends JerseyTest {

    private static final Gson gson = new Gson();
    private static final AccountService accountService = new AccountServiceImpl();

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountAPI.class);
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {
        return ServletDeploymentContext.forServlet(new ServletContainer(
                new ResourceConfig(AccountAPI.class))).build();    }


    @Test
    public void create_new_account() {
        final Response response = target("/api/v1/account").request()
                .post(Entity.json("{\"balance\":123}"));
        Account createdAccount = gson.fromJson(response.readEntity(String.class), Account.class);

        assertThat(response.getStatus(), equalTo(SC_OK));
        assertThat(createdAccount.getBalance(), equalTo(new BigDecimal(123)));
    }

    @Test
    public void get_existing_account() {
        final Account newAccount = accountService.createNewAccount(new BigDecimal(10));
        Response response = target(format("/api/v1/account/%s", newAccount.getAccountId())).request().get();
        Account accountFromResponse = gson.fromJson(response.readEntity(String.class), Account.class);

        assertThat(response.getStatus(), equalTo(SC_OK));
        assertThat(accountFromResponse.getAccountId(), equalTo(newAccount.getAccountId()));
        assertThat(accountFromResponse.getBalance(), equalTo(new BigDecimal(10)));
    }

    @Test
    public void get_not_existing_account() {
        Response response = target(format("/api/v1/account/%s", UUID.randomUUID())).request().get();

        assertThat(response.getStatus(), equalTo(SC_NOT_FOUND));
    }

    @Test
    public void transfer_money_from_existing_account_with_enough_balance() {
        final Account fromAccount = accountService.createNewAccount(new BigDecimal(50));
        final Account toAccount = accountService.createNewAccount(new BigDecimal(0));
        final String transaction = gson.toJson(new Transaction(fromAccount.getAccountId(), new BigDecimal(10)));
        final Response response = target(String.format("/api/v1/account/%s", toAccount.getAccountId())).request()
                .put(Entity.json(transaction));

        assertThat(response.getStatus(), equalTo(SC_NO_CONTENT));

        final Account fromAccountAfterTranfer = accountService.getAccount(fromAccount.getAccountId());
        final Account toAccountAfterTranfer = accountService.getAccount(toAccount.getAccountId());
        assertThat(fromAccountAfterTranfer.getBalance(), equalTo(new BigDecimal(40)));
        assertThat(toAccountAfterTranfer.getBalance(), equalTo(new BigDecimal(10)));
    }

    @Test
    public void transfer_money_from_not_existing_account() {
        final Account toAccount = accountService.createNewAccount(new BigDecimal(0));
        final String transaction = gson.toJson(new Transaction(UUID.randomUUID(), new BigDecimal(10)));
        final Response response = target(String.format("/api/v1/account/%s", toAccount.getAccountId())).request()
                .put(Entity.json(transaction));

        assertThat(response.getStatus(), equalTo(SC_FORBIDDEN));
    }

    @Test
    public void transfer_money_to_not_existing_account() {
        final Account fromAccount = accountService.createNewAccount(new BigDecimal(50));
        final String transaction = gson.toJson(new Transaction(fromAccount.getAccountId(), new BigDecimal(10)));
        final Response response = target(String.format("/api/v1/account/%s", UUID.randomUUID())).request()
                .put(Entity.json(transaction));

        assertThat(response.getStatus(), equalTo(SC_FORBIDDEN));
    }

    @Test
    public void transfer_money_from_existing_account_with_not_enough_balance() {
        final Account fromAccount = accountService.createNewAccount(new BigDecimal(50));
        final Account toAccount = accountService.createNewAccount(new BigDecimal(0));
        final String transaction = gson.toJson(new Transaction(fromAccount.getAccountId(), new BigDecimal(80)));
        final Response response = target(String.format("/api/v1/account/%s", toAccount.getAccountId())).request()
                .put(Entity.json(transaction));

        assertThat(response.getStatus(), equalTo(SC_FORBIDDEN));
    }

    @Test
    public void delete_existing_account() {
        final Account newAccount = accountService.createNewAccount(new BigDecimal(10));
        Response response = target(format("/api/v1/account/%s", newAccount.getAccountId().toString())).request().delete();

        assertThat(response.getStatus(), equalTo(SC_NO_CONTENT));
        assertThat(accountService.getAccount(newAccount.getAccountId()), is(nullValue()));
    }
}
