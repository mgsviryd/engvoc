package by.sviryd.engvoc.service.card.writer;


import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.util.StringConverterUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

@Service
public class XmlCardWriterService {

    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }

    public void write(File file, List<Card> cards, Dictionary dictionary) throws ParserConfigurationException {
        Document doc = getDocument(cards, dictionary);
        try (FileOutputStream output = new FileOutputStream(file)) {
            writeXml(doc, output);
        } catch (Exception e) {
        }
    }

    public ByteArrayInputStream dataToXml(List<Card> cards, Dictionary dictionary) throws Exception {
        byte[] date = data(cards, dictionary);
        return new ByteArrayInputStream(date);
    }
    public byte [] data(List<Card> cards, Dictionary dictionary) throws Exception {
        Document doc = getDocument(cards, dictionary);
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        return out.toByteArray();
    }


    @NotNull
    private Document getDocument(List<Card> cards, Dictionary dictionary) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        doc.setXmlVersion("1.0");
        doc.setXmlStandalone(true);


        Element rootElement = doc.createElement("dictionary");
        rootElement.setAttribute("formatVersion", "5");
        rootElement.setAttribute("title", dictionary.getName());
        rootElement.setAttribute("sourceLanguageId", dictionary.getVocabulary().getSource().getId());
        rootElement.setAttribute("destinationLanguageId", dictionary.getVocabulary().getTarget().getId());
        doc.appendChild(rootElement);
        rootElement.setAttribute("nextWordId", String.valueOf(cards.size()));
        rootElement.setAttribute("targetNamespace", "http://www.abbyy.com/TutorDictionary");
        rootElement.setAttribute("soundfile", dictionary.getName() + dictionary.getVocabulary().getCapitalizeLangPair());

        Element s = doc.createElement("statistics");
        long countLearned = cards.stream().filter(Card::isLearned).count();
        s.setAttribute("activeMeaningsQuantity", String.valueOf(cards.size() - countLearned));
        rootElement.appendChild(s);

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            Element card = doc.createElement("card");
            Element word = doc.createElement("word");
            word.setTextContent(c.getWord());
            card.appendChild(word);
            Element meanings = doc.createElement("meanings");
            Element meaning = doc.createElement("meaning");
            if (StringConverterUtil.isNotNullOrEmpty(c.getTranscription())) {
                meaning.setAttribute("transcription", c.getTranscription());
            }
            if (StringConverterUtil.isNotNullOrEmpty(c.getSound())){
                meaning.setAttribute("soundfile", "Sound" + StringUtils.capitalize(dictionary.getVocabulary().getSource().getLang()));
            }
            Element s1 = doc.createElement("statistics");
            if (c.isLearned()){
                s1.setAttribute("status", "4");
            }else{
                if(c.getCountShown() != null && c.getCountShown() > 0){
                    s1.setAttribute("status", "3");
                }else {
                    s1.setAttribute("status", "2");
                }
            }
            if (c.getCountShown() != null){
                s1.setAttribute("shown", c.getCountShown().toString());
            }
            if (c.getCountAnswered() != null){
                s1.setAttribute("answered", c.getCountAnswered().toString());
            }
            meaning.appendChild(s1);
            Element translations = doc.createElement("translations");
            Element word1 = doc.createElement("word");
            word1.setTextContent(c.getTranslation());
            translations.appendChild(word1);
            meaning.appendChild(translations);
            if (StringConverterUtil.isNotNullOrEmpty(c.getExample())) {
                Element examples = doc.createElement("examples");
                Element example = doc.createElement("example");
                example.setTextContent(c.getExample() + " — " + c.getExampleTranslation());
                examples.appendChild(example);
                meaning.appendChild(examples);
            }
            if (StringConverterUtil.isNotNullOrEmpty(c.getSound())) {
                Element sound = doc.createElement("sound");
                sound.setAttribute("name", c.getSound());
                meaning.appendChild(sound);
            }
            meanings.appendChild(meaning);
            card.appendChild(meanings);
            rootElement.appendChild(card);
        }
        doc.normalizeDocument();
        return doc;
    }
}
