        import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
        import org.semanticweb.HermiT.Reasoner;
        import org.semanticweb.owlapi.apibinding.OWLManager;
        import org.semanticweb.owlapi.expression.OWLEntityChecker;
        import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
        import org.semanticweb.owlapi.model.*;
        import org.semanticweb.owlapi.reasoner.Node;
        import org.semanticweb.owlapi.reasoner.NodeSet;
        import org.semanticweb.owlapi.reasoner.OWLReasoner;
        import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
        import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
        import org.semanticweb.owlapi.util.ShortFormProvider;
        import org.semanticweb.owlapi.util.SimpleShortFormProvider;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileReader;
        import java.io.InputStreamReader;
        import java.util.*;


public class Question2Query {

    static String query;
    static String question;

    public static String main(String args) throws Exception {

        // Load an example ontology.
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("C:\\Users\\shankar pentyala\\IdeaProjects\\kdmproject\\Data\\Sports_ontology.owl"));

        // We need a reasoner to do our query answering
        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/

        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();

        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner,shortFormProvider), shortFormProvider);
        // Enter the query loop. A user is expected to enter class
        // expression on the command line.

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
      // while (true) {

         //   System.out.println("Enter your Question?");

         //   String classExpression = br.readLine();
        String classExpression = args;
            classExpression= classExpression.replace("?","");
            question = classExpression;



            // Check for exit condition
            if (classExpression == null || classExpression.equalsIgnoreCase("x")) {
                return "Please Enter Valid Question";
            }

            //Question lemmatisation
          //  String lemma = Lemmatisation.returnLemma(classExpression);

            //reading predicate list
            BufferedReader br1 = new BufferedReader(new FileReader("C:\\KDM\\Q2Query\\data\\predicatelist"));
            String s1;
            ArrayList props = new ArrayList<String>();
            while(( s1= br1.readLine()) != null)
            {
                String s2[] = s1.split("\n");
                for(String s3:s2)
                    props.add(s3);

            }
            br1.close();
           br1 = new BufferedReader(new FileReader("C:\\KDM\\Q2Query\\data\\Objectlist"));
            ArrayList objects1 = new ArrayList<String>();
            while(( s1= br1.readLine()) != null)
            {
                String s2[] = s1.split("\n");
                for(String s3:s2)
                    objects1.add(s3);
            }

            String a[] = classExpression.split(" ");
            query ="";
            String obj ="true";

            try {
                Integer.parseInt(a[a.length-1]);

            } catch(NumberFormatException e) {
                obj ="false";
            } catch(NullPointerException e) {
                obj ="false";
            }
            // only got here if we didn't return false


        if (a.length >3) {
            if (obj.equals("false")) {
                if (objects1.contains(a[a.length - 1])) {
                    for (String x : a) {

                        if (props.contains(x)) {
                            query = x + " value " + a[a.length - 1];
                            break;
                        }
                    }
                }
            }
        }
            //DL Query
            if(query.trim().length()> 0)
           return dlQueryPrinter.askQuery(query);
           else
                return "The answer is not found...Please try other question";
           // System.out.println();
      //  }
    }

    //Query Engine for getting results after reasoning and validating owl file using Hermit reasoner
    static class DLQueryEngine {

        //Reasoner for validating owl
        //Parser for going through ontology file

        private final OWLReasoner reasoner;
        private final DLQueryParser parser;

        public DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
            this.reasoner = reasoner;
            parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
        }

        //getting super classes
        public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
            if (classExpressionString.trim().length() == 0) {
                return Collections.emptySet();
            }
            OWLClassExpression classExpression = parser
                    .parseClassExpression(classExpressionString);
            NodeSet<OWLClass> superClasses = reasoner
                    .getSuperClasses(classExpression, direct);
            return superClasses.getFlattened();
        }

        //getting equivalent classes
        public Set<OWLClass> getEquivalentClasses(String classExpressionString) {
            if (classExpressionString.trim().length() == 0) {
                return Collections.emptySet();
            }
            OWLClassExpression classExpression = parser
                    .parseClassExpression(classExpressionString);
            Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
            Set<OWLClass> result = null;
            if (classExpression.isAnonymous()) {
                result = equivalentClasses.getEntities();
            } else {
                result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
            }
            return result;
        }

        //getting subclasses
        public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct) {
            if (classExpressionString.trim().length() == 0) {
                return Collections.emptySet();
            }
            OWLClassExpression classExpression = parser
                    .parseClassExpression(classExpressionString);
            NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
            return subClasses.getFlattened();
        }

        //getting instances
        public Set<OWLNamedIndividual> getInstances(String classExpressionString,
                                                    boolean direct) {
            if (classExpressionString.trim().length() == 0) {
                return Collections.emptySet();
            }
            OWLClassExpression classExpression = parser
                    .parseClassExpression(classExpressionString);
            NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                    direct);
            return individuals.getFlattened();
        }
    }

    static class DLQueryParser {
        //creates easily readable and querable format i.e Manchester OWL syntaxed
        private final OWLOntology rootOntology;
        private final BidirectionalShortFormProvider bidiShortFormProvider;

        public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
            this.rootOntology = rootOntology;
            OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
            Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
            // Create a bidirectional short form provider to do the actual mapping.
            // It will generate names using the input
            // short form provider.
            bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                    importsClosure, shortFormProvider);
        }

        public OWLClassExpression parseClassExpression(String classExpressionString) {
            OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                    .getOWLDataFactory();
            ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                    dataFactory, classExpressionString);
            parser.setDefaultOntology(rootOntology);
            OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
            parser.setOWLEntityChecker(entityChecker);
            return parser.parseClassExpression();
        }
    }

    static class DLQueryPrinter {

        //Printing results for the query

        private final DLQueryEngine dlQueryEngine;
        private final ShortFormProvider shortFormProvider;

        public DLQueryPrinter(DLQueryEngine engine, ShortFormProvider shortFormProvider) {
            this.shortFormProvider = shortFormProvider;
            dlQueryEngine = engine;
        }

        public  String askQuery(String classExpression) {
            if (classExpression.length() == 0) {
                return "No class expression specified";
            } else {
                StringBuilder sb = new StringBuilder();

              /*  sb.append("\\nQUERY:   ").append(classExpression).append("\\n\\n");
              Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                        classExpression, false);
                printEntities("SuperClasses", superClasses, sb);
                Set<OWLClass> equivalentClasses = dlQueryEngine
                        .getEquivalentClasses(classExpression);
                printEntities("EquivalentClasses", equivalentClasses, sb);
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(classExpression,
                        true);
               printEntities("SubClasses", subClasses, sb); */
                Set<OWLNamedIndividual> individuals = dlQueryEngine.getInstances(
                        classExpression, true);
                printEntities("Instances", individuals, sb);
                //System.out.println(sb.toString());
                return sb.toString();


            }
        }

        private void printEntities(String name, Set<? extends OWLEntity> entities,
                                   StringBuilder sb) {

            // forming results

            int length = 50 - name.length();

            if (!entities.isEmpty()) {
                for (OWLEntity entity : entities) {
                    //  String s4 =
                    String [] s6 = question.split(" ");
                    s6[0] = shortFormProvider.getShortForm(entity);
                    String s7 = Arrays.toString(s6);
                    s7 = s7.replaceAll("[, \\[ \\]]"," ");
                    sb.append(s7);

                }
            } else {
                sb.append("Answer not found");
            }

        }
    }
}

