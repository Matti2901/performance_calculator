package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import javax.jcr.PropertyType;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;

public class CreateConstraint {
   
   public static ValueConstraint create(int type, String definition) throws InvalidConstraintException {
       return createConstraint(type, definition, null);
   }

   public static ValueConstraint create(int type, String jcrDefinition, NamePathResolver resolver) 
           throws InvalidConstraintException {
       return createConstraint(type, jcrDefinition, resolver);
   }

   private static ValueConstraint createConstraint(int type, String definition, NamePathResolver resolver) 
           throws InvalidConstraintException {
           
       if (definition == null) {
           throw new IllegalArgumentException("Illegal definition (null) for ValueConstraint.");
       }

       switch (type) {
           case PropertyType.STRING:
           case PropertyType.URI:
               return new StringConstraint(definition);

           case PropertyType.BOOLEAN:
               return new BooleanConstraint(definition);

           case PropertyType.BINARY:
               return new NumericConstraint(definition);

           case PropertyType.DATE:
               return new DateConstraint(definition);

           case PropertyType.LONG:
           case PropertyType.DOUBLE:
           case PropertyType.DECIMAL:
               return new NumericConstraint(definition);

           case PropertyType.NAME:
               return resolver == null ? 
                   NameConstraint.create(definition) :
                   NameConstraint.create(definition, resolver);

           case PropertyType.PATH:
               return resolver == null ? 
                   PathConstraint.create(definition) :
                   PathConstraint.create(definition, resolver);

           case PropertyType.REFERENCE:
           case PropertyType.WEAKREFERENCE:
               return resolver == null ? 
                   ReferenceConstraint.create(definition) :
                   ReferenceConstraint.create(definition, resolver);

           default:
               throw new IllegalArgumentException("Unknown/unsupported target type for constraint: " 
                       + PropertyType.nameFromValue(type));
       }
   }
   public static ValueConstraint[] create(int type, String[] definition)
           throws InvalidConstraintException {
       if (definition == null || definition.length == 0) {
           return ValueConstraint.EMPTY_ARRAY;
       }
       ValueConstraint[] ret = new ValueConstraint[definition.length];
       for (int i=0; i<ret.length; i++) {
           ret[i] = create(type, definition[i]);
       }
       return ret;
   }
   public static ValueConstraint[] create(int type, String jcrDefinition[], NamePathResolver resolver)
           throws InvalidConstraintException {
       if (jcrDefinition == null || jcrDefinition.length == 0) {
           return ValueConstraint.EMPTY_ARRAY;
       }
       ValueConstraint[] ret = new ValueConstraint[jcrDefinition.length];
       for (int i=0; i<ret.length; i++) {
           ret[i] = create(type, jcrDefinition[i], resolver);
       }
       return ret;
   }

}
