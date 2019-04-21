package com.revoluttask.api;

import com.google.gson.Gson;
import com.revoluttask.exceptions.TransferException;
import com.revoluttask.models.Account;
import com.revoluttask.models.BalanceInput;
import com.revoluttask.models.Transaction;
import com.revoluttask.services.AccountService;
import com.revoluttask.services.AccountServiceImpl;

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
import java.util.UUID;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class AccountAPI {

    private static final Gson gson = new Gson();
    private static final AccountService accountService = new AccountServiceImpl();

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
        final Account account = accountService.getAccount(UUID.fromString(id));
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

    @PUT
    @Path("/account/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoneyToAccount(@PathParam("id") String id, Transaction transaction) {
        try {
            accountService.transferMoneyFromAccount(transaction.getAmount(), transaction.getFromAccountId(), UUID.fromString(id));
        } catch (TransferException e) {
            return Response
                    .status(SC_FORBIDDEN, e.getMessage())
                    .build();
        }
        return Response
                .noContent()
                .build();
    }

    @DELETE
    @Path("/account/{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        accountService.deleteAccount(UUID.fromString(id));
        return Response
                .noContent()
                .build();
    }
}
