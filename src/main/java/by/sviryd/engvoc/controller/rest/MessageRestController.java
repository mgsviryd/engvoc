package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Message;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.repos.MessageRepo;
import by.sviryd.engvoc.type.EventType;
import by.sviryd.engvoc.type.ObjectType;
import by.sviryd.engvoc.util.WsSender;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("message")
public class MessageRestController {
    private final MessageRepo messageRepo;
    private BiConsumer<EventType, Message> wsSender;

    @Autowired
    public MessageRestController(MessageRepo messageRepo, WsSender wsSender) {
        this.messageRepo = messageRepo;
        this.wsSender = wsSender.getSender(ObjectType.MESSAGE, Views.IdText.class);
    }

    @GetMapping
    @JsonView(Views.IdText.class)
    public List<Message> list() {
        return messageRepo.findAll();
    }

    @GetMapping("{id}")
    public Message getOne(@PathVariable("id") Message message) {
        return message;
    }

    @PostMapping
    public Message create(@RequestBody Message message) {
        Message saved = messageRepo.save(message);
        wsSender.accept(EventType.CREATE, message);
        return saved;
    }

    @PutMapping("{id}")
    public Message update(@RequestBody Message message, @PathVariable("id") Message messageFromDB) {
        BeanUtils.copyProperties(message, messageFromDB, "id");
        Message saved = messageRepo.save(messageFromDB);
        wsSender.accept(EventType.UPDATE, saved);
        return saved;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Message message) {
        messageRepo.delete(message);
        wsSender.accept(EventType.REMOVE, message);
    }
}
