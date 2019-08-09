package org.autumn.mybatis.decorate;

import java.io.StringReader;

import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XmlHolder {

    private static final DocumentLoader DEFAULT_DOCUMENT_LOADER = new DefaultDocumentLoader();

    private static DocumentLoader documentLoader = DEFAULT_DOCUMENT_LOADER;

    public static void setDocumentLoader(DocumentLoader documentLoader) {
        if (null != documentLoader) {
            XmlHolder.documentLoader = documentLoader;
        }
    }

    /**
     * 将xml转换为Node
     * 需注意的是，这个Node属于新的Document，如果需要和另外的Document交互，需要先调用{@link Document#importNode(Node, boolean)}
     *
     * @param xml
     *
     * @return
     */
    public static Node string2Node(String xml) {
        try {
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = documentLoader.loadDocument(is, null, null, XmlValidationModeDetector.VALIDATION_NONE, false);
            return document.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 替换节点
     *
     * @param node
     * @param xml
     *
     * @return
     */
    public static Node replaceNode(Node node, String xml) {
        try {
            Node tmp = string2Node(xml);
            Document document = node.getOwnerDocument();
            Node newNode = document.importNode(tmp, true);
            node.getParentNode().replaceChild(newNode, node);
            return newNode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
