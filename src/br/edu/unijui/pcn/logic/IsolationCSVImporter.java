package br.edu.unijui.pcn.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IsolationCSVImporter {
   
    public static List<IsolationRecord> load(File[] files) throws InterruptedException {
        List<IsolationRecord> records = Collections.synchronizedList(new ArrayList<>());       
        
        ExecutorService executor =  Executors.newFixedThreadPool(
                Runtime.getRuntime()
                       .availableProcessors()
        );
      
        for(File file : files){
           
            executor.submit(() -> {
              
                try(BufferedReader br = new BufferedReader(new FileReader(file))){
                    
                String linha = br.readLine();   
                
                while ((linha = br.readLine()) != null) {
                    String[] partes = linha.split("[,]");
                    
                    if(partes.length >= 5 ){
                        
                        try{
                            
                            String state     = partes[0].trim();
                            String city      = partes[1].trim();
                            String isolation = partes[2].trim();
                            String date      = partes[3].trim();
                            String acronym   = partes[4].trim();                         
                            double index = Double.parseDouble(isolation);
                            
                            records.add(new IsolationRecord(state, acronym, city, index, date));
                                    
                        } catch (NumberFormatException nfe) {
                                LogManager.getLogger().log(
                                Level.WARNING,
                                "Erro de conversão na linha: " + linha,
                                nfe
                            );
                        }
                    }
                }
                } catch (IOException | NumberFormatException e) {
                    LogManager.getLogger().log(
                    Level.WARNING,
                    "Erro ao ler o arquivo: " + file.getName(),
                    e
                );
                }
            });
        }    
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        
        LogManager.getLogger().info(() -> "Importação concluída. Total de registros: "
                + records.size());
        
        return records;
    }
    
}
