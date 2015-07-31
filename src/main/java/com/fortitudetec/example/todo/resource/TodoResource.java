package com.fortitudetec.example.todo.resource;

import com.fortitudetec.example.todo.dao.TodoDao;
import com.fortitudetec.example.todo.model.Todo;
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.params.LongParam;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/todos")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TodoResource {

    private TodoDao _todoDao;

    @Context
    private UriInfo _uriInfo;

    public TodoResource(TodoDao todoDao) {
        _todoDao = todoDao;
    }

    @GET
    public List<Todo> getAllTodos() {
        return _todoDao.getAll();
    }

    @GET
    @Path("/{id}")
    public Response getTodo(@PathParam("id") LongParam id) {
        Optional<Todo> todo = _todoDao.getTodo(id.get());
        if (todo.isPresent()) {
            return Response.ok(todo.get()).build();
        }
        return notFoundResponse(id);
    }

    @POST
    public Response createTodo(@Valid Todo newTodo) {
        Todo savedTodo = _todoDao.createTodo(newTodo);
        URI location = _uriInfo.getRequestUriBuilder()
                .path(savedTodo.getId().toString())
                .build();
        return Response.created(location).entity(savedTodo).build();
    }

    @PUT
    @Path("/complete/{id}")
    public Response completeTodo(@PathParam("id") LongParam id) {
        Todo completedTodo = _todoDao.markComplete(id.get());
        return Response.ok(completedTodo).build();
    }

    @PUT
    @Path("/uncomplete/{id}")
    public Response uncompleteTodo(@PathParam("id") LongParam id) {
        Todo completedTodo = _todoDao.markIncomplete(id.get());
        return Response.ok(completedTodo).build();
    }

    private Response notFoundResponse(LongParam id) {
        Response.Status status = Response.Status.NOT_FOUND;
        return Response.status(status)
                .entity(new ErrorMessage(status.getStatusCode(), "Todo not found with id " + id))
                .type(APPLICATION_JSON)
                .build();
    }

    @VisibleForTesting
    void setUriInfo(UriInfo uriInfo) {
        _uriInfo = uriInfo;
    }
}
