package org.kie.workbench.common.widgets.client.datamodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestJavaEnum1;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestJavaEnum2;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ProjectDataModelOracle enums
 */
public class PackageDataModelOracleEnumTest {

    @Test
    public void testBasicEnums() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Person" )
                .addField( new ModelField( "age",
                                           Integer.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_NUMERIC_INTEGER ) )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addFact( "Driver" )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "Person",
                          "age",
                          new String[]{ "42", "43" } )
                .addEnum( "Person",
                          "sex",
                          new String[]{ "M", "F" } )
                .addEnum( "Driver",
                          "sex",
                          new String[]{ "M", "F" } )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        String[] personAgeEnum = oracle.getEnumValues( "Person",
                                                       "age" );
        assertEquals( 2,
                      personAgeEnum.length );
        assertEquals( "42",
                      personAgeEnum[ 0 ] );
        assertEquals( "43",
                      personAgeEnum[ 1 ] );

        String[] personSexEnum = oracle.getEnumValues( "Person",
                                                       "sex" );
        assertEquals( 2,
                      personSexEnum.length );
        assertEquals( "M",
                      personSexEnum[ 0 ] );
        assertEquals( "F",
                      personSexEnum[ 1 ] );

        String[] driverSexEnum = oracle.getEnumValues( "Driver",
                                                       "sex" );
        assertEquals( 2,
                      driverSexEnum.length );
        assertEquals( "M",
                      driverSexEnum[ 0 ] );
        assertEquals( "F",
                      driverSexEnum[ 1 ] );

    }

    @Test
    public void testBasicDependentEnums() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "field1",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addFact( "Driver" )
                .addField( new ModelField( "sex",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "'Fact.field1' : ['val1', 'val2'], 'Fact.field2' : ['val3', 'val4'], 'Fact.field2[field1=val1]' : ['f1val1a', 'f1val1b'], 'Fact.field2[field1=val2]' : ['f1val2a', 'f1val2b']",
                          Thread.currentThread().getContextClassLoader() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( "String",
                      oracle.getFieldType( "Fact",
                                           "field1" ) );
        assertEquals( "String",
                      oracle.getFieldType( "Fact",
                                           "field2" ) );

        String[] field1Enums = oracle.getEnumValues( "Fact",
                                                     "field1" );
        assertEquals( 2,
                      field1Enums.length );
        assertEquals( "val1",
                      field1Enums[ 0 ] );
        assertEquals( "val2",
                      field1Enums[ 1 ] );

        String[] field2Enums = oracle.getEnumValues( "Fact",
                                                     "field2" );
        assertEquals( 2,
                      field2Enums.length );
        assertEquals( "val3",
                      field2Enums[ 0 ] );
        assertEquals( "val4",
                      field2Enums[ 1 ] );

    }

    @Test
    public void testSmartEnums1() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "type",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "value",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "Fact",
                          "type",
                          new String[]{ "sex", "colour" } )
                .addEnum( "Fact",
                          "value[type=sex]",
                          new String[]{ "M", "F" } )
                .addEnum( "Fact",
                          "value[type=colour]",
                          new String[]{ "RED", "WHITE", "BLUE" } )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        String[] typeResult = oracle.getEnums( "Fact",
                                               "type" ).getFixedList();
        assertEquals( 2,
                      typeResult.length );
        assertEquals( "sex",
                      typeResult[ 0 ] );
        assertEquals( "colour",
                      typeResult[ 1 ] );

        Map<String, String> currentValueMap = new HashMap<String, String>();
        currentValueMap.put( "type",
                             "sex" );
        String[] typeSexResult = oracle.getEnums( "Fact",
                                                  "value",
                                                  currentValueMap ).getFixedList();
        assertEquals( 2,
                      typeSexResult.length );
        assertEquals( "M",
                      typeSexResult[ 0 ] );
        assertEquals( "F",
                      typeSexResult[ 1 ] );

        currentValueMap.clear();
        currentValueMap.put( "type",
                             "colour" );
        String[] typeColourResult = oracle.getEnums( "Fact",
                                                     "value",
                                                     currentValueMap ).getFixedList();
        assertEquals( 3,
                      typeColourResult.length );
        assertEquals( "RED",
                      typeColourResult[ 0 ] );
        assertEquals( "WHITE",
                      typeColourResult[ 1 ] );
        assertEquals( "BLUE",
                      typeColourResult[ 2 ] );
    }

    @Test
    public void testSmartEnumsDependingOnSeveralFields1() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "field1",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field3",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field4",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "Fact",
                          "field1",
                          new String[]{ "a1", "a2" } )
                .addEnum( "Fact",
                          "field2",
                          new String[]{ "b1", "b2" } )
                .addEnum( "Fact",
                          "field3[field1=a1,field2=b1]",
                          new String[]{ "c1", "c2", "c3" } )
                .addEnum( "Fact",
                          "field4[field1=a1]",
                          new String[]{ "d1", "d2" } )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        Map<String, String> currentValueMap = new HashMap<String, String>();
        currentValueMap.put( "field1",
                             "a1" );
        currentValueMap.put( "field2",
                             "b1" );

        String[] field3Result = oracle.getEnums( "Fact",
                                                 "field3",
                                                 currentValueMap ).getFixedList();
        assertEquals( 3,
                      field3Result.length );
        assertEquals( "c1",
                      field3Result[ 0 ] );
        assertEquals( "c2",
                      field3Result[ 1 ] );
        assertEquals( "c3",
                      field3Result[ 2 ] );

        String[] field4Result = oracle.getEnums( "Fact",
                                                 "field4",
                                                 currentValueMap ).getFixedList();
        assertEquals( 2,
                      field4Result.length );
        assertEquals( "d1",
                      field4Result[ 0 ] );
        assertEquals( "d2",
                      field4Result[ 1 ] );
    }

    @Test
    public void testSmartLookupEnums() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "type",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "value",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "Fact",
                          "type",
                          new String[]{ "sex", "colour" } )
                .addEnum( "Fact",
                          "value[f1, f2]",
                          new String[]{ "select something from database where x=@{f1} and y=@{f2}" } )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        Map<String, String> currentValueMap = new HashMap<String, String>();
        currentValueMap.put( "f1",
                             "f1val" );
        currentValueMap.put( "f2",
                             "f2val" );

        DropDownData dd = oracle.getEnums( "Fact",
                                           "value",
                                           currentValueMap );
        assertNull( dd.getFixedList() );
        assertNotNull( dd.getQueryExpression() );
        assertNotNull( dd.getValuePairs() );

        assertEquals( 2,
                      dd.getValuePairs().length );
        assertEquals( "select something from database where x=@{f1} and y=@{f2}",
                      dd.getQueryExpression() );
        assertEquals( "f1=f1val",
                      dd.getValuePairs()[ 0 ] );
        assertEquals( "f2=f2val",
                      dd.getValuePairs()[ 1 ] );
    }

    @Test
    public void testDataDropDown() {
        assertNull( DropDownData.create( null ) );
        assertNull( DropDownData.create( null,
                                         null ) );
        assertNotNull( DropDownData.create( new String[]{ "hey" } ) );
        assertNotNull( DropDownData.create( "abc",
                                            new String[]{ "hey" } ) );

    }

    @Test
    public void testDataHasEnums() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "field1",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "'Fact.field1' : ['val1', 'val2'], 'Fact.field2[field1=val1]' : ['f1val1a', 'f1val1b'], 'Fact.field2[field1=val2]' : ['f1val2a', 'f1val2b']",
                          Thread.currentThread().getContextClassLoader() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        //Fact.field1 has explicit enumerations
        assertTrue( oracle.hasEnums( "Fact.field1" ) );
        assertTrue( oracle.hasEnums( "Fact",
                                     "field1" ) );

        //Fact.field2 has explicit enumerations dependent upon Fact.field1
        assertTrue( oracle.hasEnums( "Fact.field2" ) );
        assertTrue( oracle.hasEnums( "Fact",
                                     "field2" ) );
    }

    @Test
    public void testDataHasEnumsFieldSuffixes() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "field1",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2suffixed",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "'Fact.field1' : ['val1', 'val2'], 'Fact.field2[field1=val1]' : ['f1val1a', 'f1val1b']",
                          Thread.currentThread().getContextClassLoader() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        //Fact.field1 has explicit enumerations
        assertTrue( oracle.hasEnums( "Fact.field1" ) );
        assertTrue( oracle.hasEnums( "Fact",
                                     "field1" ) );

        //Fact.field2 has explicit enumerations dependent upon Fact.field1
        assertTrue( oracle.hasEnums( "Fact.field2" ) );
        assertTrue( oracle.hasEnums( "Fact",
                                     "field2" ) );

        //Fact.field2suffixed has no enums
        assertFalse( oracle.hasEnums( "Fact.field2suffixed" ) );
        assertFalse( oracle.hasEnums( "Fact",
                                      "field2suffixed" ) );
    }

    @Test
    public void testDependentEnums() {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addFact( "Fact" )
                .addField( new ModelField( "field1",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field2",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field3",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .addField( new ModelField( "field4",
                                           String.class.getName(),
                                           ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                           ModelField.FIELD_ORIGIN.DECLARED,
                                           FieldAccessorsAndMutators.BOTH,
                                           DataType.TYPE_STRING ) )
                .end()
                .addEnum( "'Fact.field1' : ['val1', 'val2']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field2[field1=val1]' : ['f1val1a', 'f1val1b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field2[field1=val2]' : ['f1val2a', 'f1val2b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field3[field2=f1val1a]' : ['f1val1a1a', 'f1val1a1b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field3[field2=f1val1b]' : ['f1val1b1a', 'f1val1b1b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field3[field2=f1val2a]' : ['f1val2a1a', 'f1val2a1b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field3[field2=f1val2b]' : ['f1val2a2a', 'f1val2b2b']",
                          Thread.currentThread().getContextClassLoader() )
                .addEnum( "'Fact.field4' : ['f4val1', 'f4val2']",
                          Thread.currentThread().getContextClassLoader() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertTrue( oracle.isDependentEnum( "Fact",
                                            "field1",
                                            "field2" ) );
        assertTrue( oracle.isDependentEnum( "Fact",
                                            "field1",
                                            "field3" ) );
        assertTrue( oracle.isDependentEnum( "Fact",
                                            "field2",
                                            "field3" ) );
        assertFalse( oracle.isDependentEnum( "Fact",
                                             "field1",
                                             "field4" ) );
    }

    @Test
    public void testJavaEnum1() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestJavaEnum1.class,
                           new HashMap<String, FactBuilder>() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 2,
                      oracle.getFactTypes().length );
        assertEquals( TestJavaEnum1.class.getSimpleName(),
                      oracle.getFactTypes()[ 1 ] );

        final DropDownData dd = oracle.getEnums( TestJavaEnum1.class.getSimpleName(),
                                                 "field1" );
        assertNotNull( dd );
        assertEquals( 3,
                      dd.getFixedList().length );
        assertEquals( "TestEnum.ZERO=TestEnum.ZERO",
                      dd.getFixedList()[ 0 ] );
        assertEquals( "TestEnum.ONE=TestEnum.ONE",
                      dd.getFixedList()[ 1 ] );
        assertEquals( "TestEnum.TWO=TestEnum.TWO",
                      dd.getFixedList()[ 2 ] );

        final String[] ddValues = oracle.getEnumValues( TestJavaEnum1.class.getSimpleName(),
                                                        "field1" );
        assertNotNull( ddValues );
        assertEquals( 3,
                      ddValues.length );
        assertEquals( "TestEnum.ZERO=TestEnum.ZERO",
                      ddValues[ 0 ] );
        assertEquals( "TestEnum.ONE=TestEnum.ONE",
                      ddValues[ 1 ] );
        assertEquals( "TestEnum.TWO=TestEnum.TWO",
                      ddValues[ 2 ] );
    }

    @Test
    public void testJavaEnum2() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestJavaEnum2.class,
                           new HashMap<String, FactBuilder>() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setJavaEnumDefinitions( packageLoader.getProjectJavaEnumDefinitions() );
        dataModel.setWorkbenchEnumDefinitions( packageLoader.getPackageWorkbenchDefinitions() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 2,
                      oracle.getFactTypes().length );
        assertEquals( TestJavaEnum2.class.getSimpleName(),
                      oracle.getFactTypes()[ 1 ] );

        final DropDownData dd = oracle.getEnums( TestJavaEnum2.class.getSimpleName(),
                                                 "field1" );
        assertNotNull( dd );
        assertEquals( 3,
                      dd.getFixedList().length );
        assertEquals( "TestExternalEnum.ZERO=TestExternalEnum.ZERO",
                      dd.getFixedList()[ 0 ] );
        assertEquals( "TestExternalEnum.ONE=TestExternalEnum.ONE",
                      dd.getFixedList()[ 1 ] );
        assertEquals( "TestExternalEnum.TWO=TestExternalEnum.TWO",
                      dd.getFixedList()[ 2 ] );

        final String[] ddValues = oracle.getEnumValues( TestJavaEnum2.class.getSimpleName(),
                                                        "field1" );
        assertNotNull( ddValues );
        assertEquals( 3,
                      ddValues.length );
        assertEquals( "TestExternalEnum.ZERO=TestExternalEnum.ZERO",
                      ddValues[ 0 ] );
        assertEquals( "TestExternalEnum.ONE=TestExternalEnum.ONE",
                      ddValues[ 1 ] );
        assertEquals( "TestExternalEnum.TWO=TestExternalEnum.TWO",
                      ddValues[ 2 ] );
    }

}
