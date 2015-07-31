package com.fortitudetec.example.todo.resource;

import com.fortitudetec.example.todo.dao.TodoDao;
import com.fortitudetec.example.todo.model.Todo;
import com.google.common.collect.Lists;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.params.LongParam;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoResourceTest {

    private TodoResource _todoResource;
    private TodoDao _todoDao;
    private UriInfo _uriInfo;

    @Before
    public void setUp() {
        _todoDao = mock(TodoDao.class);
        _todoResource = new TodoResource(_todoDao);
        _uriInfo = mock(UriInfo.class);
        _todoResource.setUriInfo(_uriInfo);
    }

    @Test
    public void testGetAllTodos() {
        List<Todo> todos = Lists.newArrayList();
        when(_todoDao.getAll()).thenReturn(todos);

        List<Todo> allTodos = _todoResource.getAllTodos();

        assertThat(allTodos).isSameAs(todos);
    }

    @Test
    public void testGetTodo_WhenFound() {
        Long id = 42L;
        Todo todo = new Todo();
        when(_todoDao.getTodo(id)).thenReturn(Optional.of(todo));

        Response response = _todoResource.getTodo(new LongParam(id.toString()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isSameAs(todo);
    }

    @Test
    public void testGetTodo_WhenNotFound() {
        Long id = 42L;
        when(_todoDao.getTodo(id)).thenReturn(Optional.empty());

        Response response = _todoResource.getTodo(new LongParam(id.toString()));
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);
        assertThat(response.getEntity()).isInstanceOf(ErrorMessage.class);
    }

    @Test
    public void testCreateTodo() {
        Todo todo = new Todo();
        Long id = 42L;
        Todo savedTodo = new Todo();
        savedTodo.setId(id);

        String path = "/todos";
        when(_uriInfo.getRequestUriBuilder()).thenReturn(UriBuilder.fromPath(path));

        when(_todoDao.createTodo(todo)).thenReturn(savedTodo);

        Response response = _todoResource.createTodo(todo);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getLocation().toString()).isEqualTo(path + "/" + id);
        assertThat(response.getEntity()).isSameAs(savedTodo);
    }

    @Test
    public void testCompleteTodo() {
        Todo completeTodo = new Todo();
        completeTodo.setCompleted(true);

        Long id = 42L;

        when(_todoDao.markComplete(id)).thenReturn(completeTodo);

        Response response = _todoResource.completeTodo(new LongParam(id.toString()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isSameAs(completeTodo);
    }

    @Test
    public void testUncompleteTodo() {
        Todo incompleteTodo = new Todo();
        incompleteTodo.setCompleted(false);

        Long id = 42L;

        when(_todoDao.markComplete(id)).thenReturn(incompleteTodo);

        Response response = _todoResource.completeTodo(new LongParam(id.toString()));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isSameAs(incompleteTodo);
    }

}