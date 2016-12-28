//package cs.sii.model.bot;
//
//
//import javax.persistence.AttributeConverter;
//import javax.persistence.Converter;
//
//@Converter
//public class BooleanConverter implements AttributeConverter<Boolean, Integer>  {
//
//	@Override
//	public Integer convertToDatabaseColumn(Boolean attribute) {
//		if(attribute)
//			return 1;
//		return 0;
//	}
//
//
//	@Override
//	public Boolean convertToEntityAttribute(Integer dbData) {
//		if(dbData==1)
//			return Boolean.TRUE;
//		return Boolean.FALSE;
//	}
//
//	
//}
