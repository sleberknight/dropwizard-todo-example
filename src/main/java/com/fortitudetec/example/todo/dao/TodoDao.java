package com.fortitudetec.example.todo.dao;

import com.fortitudetec.example.todo.model.Todo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TodoDao {

    @Autowired
    private SessionFactory _sessionFactory;

    private Session currentSession() {
        return _sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    public Optional<Todo> getTodo(Long id) {
        Todo todo = (Todo) currentSession().get(Todo.class, id);
        return Optional.ofNullable(todo);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Todo> getAll() {
        return currentSession().createCriteria(Todo.class).list();
    }

    @Transactional
    public Todo createTodo(Todo newTodo) {
        currentSession().save(newTodo);
        return newTodo;
    }

    @Transactional
    public Todo markComplete(Long id) {
        return markWithCompletionValue(id, true);
    }

    @Transactional
    public Todo markIncomplete(Long id) {
        return markWithCompletionValue(id, false);
    }

    private Todo markWithCompletionValue(Long id, boolean complete) {
        Todo todo = (Todo) currentSession().get(Todo.class, id);
        todo.setCompleted(complete);
        currentSession().merge(todo);
        return todo;
    }
}
