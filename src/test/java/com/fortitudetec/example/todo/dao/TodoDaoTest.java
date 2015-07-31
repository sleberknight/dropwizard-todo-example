package com.fortitudetec.example.todo.dao;

import com.fortitudetec.example.todo.TodoSpringConfiguration;
import com.fortitudetec.example.todo.model.Todo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {TestSpringConfiguration.class, TodoSpringConfiguration.class})
public class TodoDaoTest extends AbstractHibernateDaoTest {

    @Autowired
    private TodoDao _todoDao;

    @Test
    public void testCreateTodo() {
        Todo todo = new Todo();
        todo.setItem("Do laundry");
        Todo savedTodo = _todoDao.createTodo(todo);

        assertThat(savedTodo.getId()).isNotNull();
        assertThat(savedTodo.isCompleted()).isFalse();
    }

    @Test
    public void testGetTodo() {
        Todo todo = saveNewTodo("Buy oranges");
        Long id = todo.getId();

        Optional<Todo> foundTodo = _todoDao.getTodo(id);
        assertThat(foundTodo.isPresent()).isTrue();
        assertThat(foundTodo.get().getItem()).isEqualTo("Buy oranges");
    }

    @Test
    public void testGetTodo_WhenDoesNotExist() {
        Long id = -42L;

        Optional<Todo> foundTodo = _todoDao.getTodo(id);
        assertThat(foundTodo.isPresent()).isFalse();
    }

    @Test
    public void testGetAll() {
        int originalCount = countRowsInTable("todos");

        saveNewTodo("Buy oranges");
        saveNewTodo("Buy bananas");
        saveNewTodo("Buy apples");

        int expectedCount = 3 + originalCount;

        List<Todo> todos = _todoDao.getAll();
        assertThat(todos).hasSize(expectedCount);
    }

    @Test
    public void testMarkComplete() {
        Todo todo = saveNewTodo("Buy oranges", false);
        assertThat(todo.isCompleted()).isFalse();

        Todo updatedTodo = _todoDao.markComplete(todo.getId());
        assertThat(updatedTodo.isCompleted()).isTrue();
    }

    @Test
    public void testMarkIncomplete() {
        Todo todo = saveNewTodo("Buy oranges", true);
        assertThat(todo.isCompleted()).isTrue();

        Todo updatedTodo = _todoDao.markIncomplete(todo.getId());
        assertThat(updatedTodo.isCompleted()).isFalse();
    }

    private Todo saveNewTodo(String item) {
        return saveNewTodo(item, false);
    }

    private Todo saveNewTodo(String item, boolean complete) {
        Todo todo = new Todo();
        todo.setItem(item);
        todo.setCompleted(complete);

        getCurrentSession().save(todo);
        flushAndClearSession();

        return todo;
    }

}