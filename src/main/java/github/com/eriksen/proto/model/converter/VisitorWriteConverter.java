package github.com.eriksen.proto.model.converter;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import github.com.eriksen.proto.model.Visitor;

/**
 * VisitorWriteConverter
 */
@Component
public class VisitorWriteConverter implements Converter<Visitor, Document>{

  @Override
  public Document convert(Visitor source) {
    Document doc = new Document();
    doc.put("customer", new ObjectId(source.getCustomer()));
    doc.put("createdTime", source.getCreatedTime());
    doc.put("lastModTime", source.getLastModTime());
    return doc;
  }
}