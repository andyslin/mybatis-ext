package org.autumn.mybatis.decorate;

import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

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
     * @return
     */
    public static NodeList string2Node(String xml) {
        try {
            xml = "<virual-root>" + xml + "</virual-root>";
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = documentLoader.loadDocument(is, null, null, XmlValidationModeDetector.VALIDATION_NONE, false);
            return document.getDocumentElement().getChildNodes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 替换节点
     *
     * @param node
     * @param xml
     * @return
     */
    public static void replaceNode(Node node, String xml) {
        try {
            NodeList tmp = string2Node(xml);
            int max = tmp.getLength() - 1;
            Document document = node.getOwnerDocument();
            Node parentNode = node.getParentNode();
            Node newNode = null;
            for (int i = max; i >= 0; i--) {
                Node item = tmp.item(i);
                newNode = document.importNode(item, true);
                if (i == max) {
                    parentNode.replaceChild(newNode, node);
                } else {
                    parentNode.insertBefore(newNode, node);
                }
                node = newNode;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
