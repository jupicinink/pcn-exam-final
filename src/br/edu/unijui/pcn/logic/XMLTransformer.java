package br.edu.unijui.pcn.logic;

import br.edu.unijui.pcn.utils.XMLHandler;
import java.sql.SQLException;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.logging.Level;

public class XMLTransformer {
    
    
    private final DBManager dbManager;

    public XMLTransformer(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void export(String fileName) {

        try {

            List<IsolationRecord> records =
                    dbManager.getAllRecords();

            Document doc =
                    XMLHandler.newDocument();
            
            if (doc == null) {
                throw new RuntimeException(
                    "Não foi possível criar o documento XML."
                );
            }

            // Elemento raiz
            Element root =
                    doc.createElement("isolation-indexes");

            doc.appendChild(root);

            int id = 1;

            for (IsolationRecord record : records) {

                Element covid =
                        doc.createElement("covid");

                covid.setAttribute(
                        "id",
                        String.valueOf(id++)
                );

                covid.setAttribute(
                        "city",
                        record.city()
                );

                covid.setAttribute(
                        "state-name",
                        record.state()
                );

                covid.setAttribute(
                        "state-acronym",
                        record.stateAcronym()
                );

                covid.setAttribute(
                        "date",
                        record.date()
                );

                covid.setAttribute(
                        "index",
                        String.valueOf(record.index())
                );

                root.appendChild(covid);
            }

            if (!fileName.endsWith(".xml")) {
                fileName += ".xml";
            }

            XMLHandler.writeXmlFile(doc, fileName);

                    LogManager.getLogger().info("Arquivo XML exportado: "+ fileName);

        } catch (SQLException e) {

            LogManager.getLogger().log(
            Level.WARNING,
            "Erro ao exportar XML",
            e
        );
        }
    }
}
