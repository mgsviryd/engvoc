package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.repos.CardRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CardService {
    @Autowired
    private CardRepo cardRepo;


    public long count() {
        return cardRepo.count();
    }

    public void delete(Card card) {
        cardRepo.delete(card);
    }

    public List<Card> findAll() {
        return cardRepo.findAll();
    }

    public List<Card> findAllById(List<Long> ids) {
        return cardRepo.findAllById(ids);
    }

    public Card save(Card card) {
        return cardRepo.save(card);
    }

    public List<Card> saveAll(List<Card> cards) {
        return cardRepo.saveAll(cards);
    }

    public Page<Card> getCards(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return cardRepo.findAll(pageable);
    }

    public Page<Card> getCardsByDictionary(Dictionary dictionary, Pageable pageable) {
        return cardRepo.getCardsByDictionary(dictionary, pageable);
    }
    public List<Card> getCardsByDictionary(Dictionary dictionary) {
        return cardRepo.getCardsByDictionary(dictionary);
    }

    public Page<Card> findAll(Pageable pageable) {
        return cardRepo.findAll(pageable);
    }
}