package api;

import com.google.gson.Gson;
import exceptions.TransferException;
import models.Account;
import models.BalanceInput;
import models.Transaction;
import services.AccountService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static sun.security.timestamp.TSResponse.BAD_REQUEST;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class AccountAPI {

    private final AccountService accountService;
    private static final Gson gson = new Gson();

    public AccountAPI() {
        this.accountService = AccountService.getInstance();
    }

    @GET
    @Path("/account")
    public Response getAllAccounts() {
        return Response
                .ok()
                .entity(gson.toJson(accountService.getAllAccounts()))
                .build();
    }

    @GET
    @Path("/account/{id}")
    public Response getAccount(@PathParam("id") String id) {
        final Account account = accountService.getAccount(id);
        if (account == null) {
            return Response
                    .status(NOT_FOUND)
                    .build();
        } else {
            return Response
                    .ok()
                    .entity(gson.toJson(account))
                    .build();
        }
    }

    @POST
    @Path("/account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(BalanceInput balanceInput) {
        final Account account = accountService.createNewAccount(balanceInput.getBalance());
        return Response
                .ok()
                .entity(gson.toJson(account))
                .build();
    }

    @DELETE
    @Path("/account/{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        accountService.deleteAccount(id);
        return Response
                .noContent()
                .build();
    }

    @PUT
    @Path("/account/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoneyToAccount(@PathParam("id") String id, Transaction transaction) {
        try {
            accountService.transferMoneyFromAccount(transaction.getMoneyAmount(), transaction.getFromAccountId(), id);
        } catch (TransferException e) {
            return Response
                    .status(BAD_REQUEST, e.getMessage())
                    .build();
        }
        return Response
                .noContent()
                .build();
    }
}
