package org.kie.workbench.common.widgets.client.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Product;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestDataTypes;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestDelegatedClass;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestDirectRecursionClass;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestIndirectRecursionClassA;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestSubClass;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.TestSuperClass;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ProjectDataModelOracle
 */
public class PackageDataModelOracleTest {

    @Test
    public void testDataTypes() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestDataTypes.class,
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
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestDataTypes.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestDataTypes.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 20,
                                                          fields.length );
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldString" ) );
        assertEquals( DataType.TYPE_BOOLEAN,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldBooleanObject" ) );
        assertEquals( DataType.TYPE_DATE,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldDate" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldNumeric" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGDECIMAL,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldBigDecimal" ) );
        assertEquals( DataType.TYPE_NUMERIC_BIGINTEGER,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldBigInteger" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldByteObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldDoubleObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldFloatObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldIntegerObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldLongObject" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldShortObject" ) );
        assertEquals( DataType.TYPE_BOOLEAN,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldBooleanPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_BYTE,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldBytePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_DOUBLE,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldDoublePrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldFloatPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldIntegerPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_LONG,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldLongPrimitive" ) );
        assertEquals( DataType.TYPE_NUMERIC_SHORT,
                      oracle.getFieldType( TestDataTypes.class.getSimpleName(),
                                           "fieldShortPrimitive" ) );
    }

    @Test
    public void testSuperClass() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.class,
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
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 3,
                                                          fields.length );
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestSuperClass.class.getSimpleName(),
                                           "this" ) );
        assertEquals( TestSuperClass.class.getSimpleName(),
                      oracle.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                                "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestSuperClass.class.getSimpleName(),
                                           "field1" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                                "field1" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      oracle.getFieldType( TestSuperClass.class.getSimpleName(),
                                           "list" ) );
        assertEquals( List.class.getName(),
                      oracle.getFieldClassName( TestSuperClass.class.getSimpleName(),
                                                "list" ) );
        assertEquals( String.class.getName(),
                      oracle.getParametricFieldType( TestSuperClass.class.getSimpleName(),
                                                     "list" ) );
    }

    @Test
    public void testSubClass() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSubClass.class,
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
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSubClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSubClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 4,
                                                          fields.length );
                                            for ( ModelField field : fields ) {
                                                if ( "this".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.SELF, field.getOrigin() );
                                                } else if ( "field1".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.INHERITED, field.getOrigin() );
                                                } else if ( "field2".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.DECLARED, field.getOrigin() );
                                                }
                                                // TODO this last case is arguable : should be inherited, but is qualified as delegated, probably needs to be looked at
                                                // else if ("list".equals(field.getName())) assertEquals(ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin());
                                            }
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestSubClass.class.getSimpleName(),
                                           "this" ) );
        assertEquals( TestSubClass.class.getSimpleName(),
                      oracle.getFieldClassName( TestSubClass.class.getSimpleName(),
                                                "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestSubClass.class.getSimpleName(),
                                           "field1" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestSubClass.class.getSimpleName(),
                                                "field1" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestSubClass.class.getSimpleName(),
                                           "field2" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestSubClass.class.getSimpleName(),
                                                "field2" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      oracle.getFieldType( TestSubClass.class.getSimpleName(),
                                           "list" ) );
        assertEquals( List.class.getName(),
                      oracle.getFieldClassName( TestSubClass.class.getSimpleName(),
                                                "list" ) );
        assertEquals( String.class.getName(),
                      oracle.getParametricFieldType( TestSubClass.class.getSimpleName(),
                                                     "list" ) );
    }

    @Test
    public void testDelegatedClass() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestDelegatedClass.class,
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
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestDelegatedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestDelegatedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 3,
                                                          fields.length );
                                            for ( ModelField field : fields ) {
                                                if ( "this".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.SELF, field.getOrigin() );
                                                } else if ( "field1".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin() );
                                                } else if ( "list".equals( field.getName() ) ) {
                                                    assertEquals( ModelField.FIELD_ORIGIN.DELEGATED, field.getOrigin() );
                                                }
                                            }
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                           "this" ) );
        assertEquals( TestDelegatedClass.class.getSimpleName(),
                      oracle.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                                "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                           "field1" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                                "field1" ) );
        assertEquals( DataType.TYPE_COLLECTION,
                      oracle.getFieldType( TestDelegatedClass.class.getSimpleName(),
                                           "list" ) );
        assertEquals( List.class.getName(),
                      oracle.getFieldClassName( TestDelegatedClass.class.getSimpleName(),
                                                "list" ) );
        assertEquals( String.class.getName(),
                      oracle.getParametricFieldType( TestDelegatedClass.class.getSimpleName(),
                                                     "list" ) );
    }

    @Test
    public void testNestedClass() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.NestedClass.class,
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
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.NestedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestSuperClass.NestedClass.class.getSimpleName(),
                                           "this" ) );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFieldClassName( TestSuperClass.NestedClass.class.getSimpleName(),
                                                "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestSuperClass.NestedClass.class.getSimpleName(),
                                           "nestedField1" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestSuperClass.NestedClass.class.getSimpleName(),
                                                "nestedField1" ) );
    }

    @Test
    public void testImportedNestedClass() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.NestedClass.class,
                           new HashMap<String, FactBuilder>() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );

        final HasImports hasImports = new HasImports() {

            final Imports imports = new Imports();

            {
                imports.addImport( new Import( "org.kie.workbench.common.widgets.client.datamodel.testclasses.TestSuperClass.NestedClass" ) );
            }

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( Imports imports ) {
            }
        };

        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 hasImports,
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.NestedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                        }
                                    } );

        assertEquals( DataType.TYPE_THIS,
                      oracle.getFieldType( TestSuperClass.NestedClass.class.getSimpleName(),
                                           "this" ) );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFieldClassName( TestSuperClass.NestedClass.class.getSimpleName(),
                                                "this" ) );
        assertEquals( DataType.TYPE_STRING,
                      oracle.getFieldType( TestSuperClass.NestedClass.class.getSimpleName(),
                                           "nestedField1" ) );
        assertEquals( String.class.getName(),
                      oracle.getFieldClassName( TestSuperClass.NestedClass.class.getSimpleName(),
                                                "nestedField1" ) );
    }

    @Test
    public void testImportedNestedClassMethodInformation() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.NestedClass.class,
                           new HashMap<String, FactBuilder>() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setMethodInformation( packageLoader.getProjectMethodInformation() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );

        final HasImports hasImports = new HasImports() {

            final Imports imports = new Imports();

            {
                imports.addImport( new Import( "org.kie.workbench.common.widgets.client.datamodel.testclasses.TestSuperClass.NestedClass" ) );
            }

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( Imports imports ) {
            }
        };

        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 hasImports,
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.NestedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                        }
                                    } );
        oracle.getMethodInfos( TestSuperClass.NestedClass.class.getSimpleName(),
                               new Callback<List<MethodInfo>>() {
                                   @Override
                                   public void callback( final List<MethodInfo> methodInfos ) {
                                       assertNotNull( methodInfos );
                                       assertEquals( 3,
                                                     methodInfos.size() );
                                       //Use SimpleName as the return type has been imported
                                       final MethodInfo mf0 = new MethodInfo( "methodDoingSomethingThatReturnsAnInnerClass",
                                                                              new ArrayList<String>(),
                                                                              TestSuperClass.NestedClass.class.getSimpleName(),
                                                                              null,
                                                                              TestSuperClass.NestedClass.class.getSimpleName() );

                                       //Use FQCN as the return type has not been imported
                                       final MethodInfo mf1 = new MethodInfo( "methodDoingSomethingWithNestedField1",
                                                                              new ArrayList<String>(),
                                                                              String.class,
                                                                              null,
                                                                              DataType.TYPE_STRING );

                                       //Use FQCN as the return type has not been imported
                                       final MethodInfo mf2 = new MethodInfo( "methodDoingSomethingThatReturnsAnOuterClass",
                                                                              new ArrayList<String>(),
                                                                              Product.class.getName(),
                                                                              null,
                                                                              Product.class.getName() );
                                       assertTrue( methodInfos.contains( mf0 ) );
                                       assertTrue( methodInfos.contains( mf1 ) );
                                       assertTrue( methodInfos.contains( mf2 ) );
                                   }
                               } );
    }

    @Test
    public void testImportedNestedClassMethodInformationImportBothTypes() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.NestedClass.class,
                           new HashMap<String, FactBuilder>() )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder().setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setMethodInformation( packageLoader.getProjectMethodInformation() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );

        final HasImports hasImports = new HasImports() {

            final Imports imports = new Imports();

            {
                imports.addImport( new Import( "org.kie.workbench.common.widgets.client.datamodel.testclasses.TestSuperClass.NestedClass" ) );
                imports.addImport( new Import( "org.kie.workbench.common.widgets.client.datamodel.testclasses.Product" ) );
            }

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( Imports imports ) {
            }
        };

        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 hasImports,
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.NestedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                        }
                                    } );
        oracle.getMethodInfos( TestSuperClass.NestedClass.class.getSimpleName(),
                               new Callback<List<MethodInfo>>() {
                                   @Override
                                   public void callback( final List<MethodInfo> methodInfos ) {
                                       assertNotNull( methodInfos );
                                       assertEquals( 3,
                                                     methodInfos.size() );
                                       //Use SimpleName as the return type has been imported
                                       final MethodInfo mf0 = new MethodInfo( "methodDoingSomethingThatReturnsAnInnerClass",
                                                                              new ArrayList<String>(),
                                                                              TestSuperClass.NestedClass.class.getSimpleName(),
                                                                              null,
                                                                              TestSuperClass.NestedClass.class.getSimpleName() );

                                       //Use SimpleName as the return type has been imported
                                       final MethodInfo mf1 = new MethodInfo( "methodDoingSomethingThatReturnsAnOuterClass",
                                                                              new ArrayList<String>(),
                                                                              Product.class.getSimpleName(),
                                                                              null,
                                                                              Product.class.getSimpleName() );

                                       //Use FQCN as the return type has not been imported
                                       final MethodInfo mf2 = new MethodInfo( "methodDoingSomethingWithNestedField1",
                                                                              new ArrayList<String>(),
                                                                              String.class,
                                                                              null,
                                                                              DataType.TYPE_STRING );

                                       assertTrue( methodInfos.contains( mf0 ) );
                                       assertTrue( methodInfos.contains( mf1 ) );
                                       assertTrue( methodInfos.contains( mf2 ) );
                                   }
                               } );
    }

    @Test
    public void testImportedNestedClassMethodInformationInPackageScope() throws IOException {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TestSuperClass.NestedClass.class,
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
        dataModel.setMethodInformation( packageLoader.getProjectMethodInformation() );
        dataModel.setFieldParametersType( packageLoader.getProjectFieldParametersType() );

        final HasImports hasImports = new HasImports() {

            final Imports imports = new Imports();

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( Imports imports ) {
            }
        };

        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 hasImports,
                                                                 oracle,
                                                                 dataModel );
        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( TestSuperClass.NestedClass.class.getSimpleName(),
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( TestSuperClass.NestedClass.class.getSimpleName(),
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                        }
                                    } );
        oracle.getMethodInfos( TestSuperClass.NestedClass.class.getSimpleName(),
                               new Callback<List<MethodInfo>>() {
                                   @Override
                                   public void callback( final List<MethodInfo> methodInfos ) {
                                       assertNotNull( methodInfos );
                                       assertEquals( 3,
                                                     methodInfos.size() );
                                       //Use SimpleName as the return type is in the same package represented by the Oracle
                                       final MethodInfo mf0 = new MethodInfo( "methodDoingSomethingThatReturnsAnInnerClass",
                                                                              new ArrayList<String>(),
                                                                              TestSuperClass.NestedClass.class.getSimpleName(),
                                                                              null,
                                                                              TestSuperClass.NestedClass.class.getSimpleName() );

                                       //Use SimpleName as the return type is in the same package represented by the Oracle
                                       final MethodInfo mf1 = new MethodInfo( "methodDoingSomethingThatReturnsAnOuterClass",
                                                                              new ArrayList<String>(),
                                                                              Product.class.getSimpleName(),
                                                                              null,
                                                                              Product.class.getSimpleName() );

                                       //Use FQCN as the return type has not been imported
                                       final MethodInfo mf2 = new MethodInfo( "methodDoingSomethingWithNestedField1",
                                                                              new ArrayList<String>(),
                                                                              String.class,
                                                                              null,
                                                                              DataType.TYPE_STRING );

                                       assertTrue( methodInfos.contains( mf0 ) );
                                       assertTrue( methodInfos.contains( mf1 ) );
                                       assertTrue( methodInfos.contains( mf2 ) );
                                   }
                               } );
    }

    @Test
    public void testDirectRecursion() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          new HashMap<String, FactBuilder>(),
                                                          TestDirectRecursionClass.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" );
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "TestDirectRecursionClass",
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( "TestDirectRecursionClass",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] result ) {
                                            assertEquals( 2,
                                                          result.length );
                                            assertEquals( "TestDirectRecursionClass",
                                                          result[ 0 ].getClassName() );
                                            assertEquals( DataType.TYPE_THIS,
                                                          result[ 0 ].getName() );
                                            assertEquals( "TestDirectRecursionClass",
                                                          result[ 1 ].getClassName() );
                                            assertEquals( "recursiveField",
                                                          result[ 1 ].getName() );
                                        }
                                    } );
    }

    @Test
    public void testIndirectRecursion() throws Exception {
        //Build ProjectDMO
        final ProjectDataModelOracleBuilder projectBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        final ProjectDataModelOracleImpl projectLoader = new ProjectDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder( projectBuilder,
                                                          new HashMap<String, FactBuilder>(),
                                                          TestIndirectRecursionClassA.class,
                                                          false,
                                                          TypeSource.JAVA_PROJECT );
        cb.build( projectLoader );

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder( "org.kie.workbench.common.widgets.client.datamodel.testclasses" );
        packageBuilder.setProjectOracle( projectLoader );
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setTypeAnnotations( packageLoader.getProjectTypeAnnotations() );
        dataModel.setTypeFieldsAnnotations( packageLoader.getProjectTypeFieldsAnnotations() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertEquals( "TestIndirectRecursionClassA",
                      oracle.getFactTypes()[ 0 ] );

        oracle.getFieldCompletions( "TestIndirectRecursionClassA",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] result ) {
                                            assertEquals( 2,
                                                          result.length );
                                            assertEquals( "TestIndirectRecursionClassA",
                                                          result[ 0 ].getClassName() );
                                            assertEquals( DataType.TYPE_THIS,
                                                          result[ 0 ].getName() );
                                            assertEquals( "TestIndirectRecursionClassB",
                                                          result[ 1 ].getClassName() );
                                            assertEquals( "recursiveField",
                                                          result[ 1 ].getName() );
                                        }
                                    } );
    }

}
