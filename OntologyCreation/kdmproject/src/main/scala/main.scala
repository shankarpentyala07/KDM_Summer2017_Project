


import java.io.{FileOutputStream, PrintStream}
import java.util

import org.apache.spark.{SparkConf, SparkContext}
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.simple.Document

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi._
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.util.DefaultPrefixManager
import org.semanticweb.owlapi.model._

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.HashSet
import scala.io.Source

/**
  * Created by shankar pentyala on 22-07-2017.
  */
object main {
  def main(args : Array[String]): Unit =
  {
    System.setProperty("hadoop.home.dir","F:\\winutils")
    val sparkConf = new SparkConf().setAppName("Q&A system").setMaster("local[*]").
      set("spark.driver.memory", "6g").set("spark.executor.memory", "6g")
    val sc = new SparkContext(sparkConf)
    val stopWordsInput = sc.textFile("C:\\Users\\shankar pentyala\\IdeaProjects\\QASystem\\DataFiles\\stopwords.txt")
    val stopwords = stopWordsInput.flatMap(x=>x.split(",")).map(_.trim)
    val broadcastStopWords = sc.broadcast(stopwords.collect.toSet)
   val input = sc.textFile("C:\\KDM\\WikiRef_dataset\\WikiRef150\\tohoku.earthquake.tsunami.3.txt")
    val classes_outfile = new PrintStream("Data/classes2.txt")
    val Individuals_outfile = new PrintStream("Data/Individuals2.txt")
    val triplets_outfile = new PrintStream("Data/triplets2.txt")
    val objprop_outfile = new PrintStream("Data/ObjProp2.txt")
    val predlist_outfile = new PrintStream("Data/Predicates2.txt")
    val Dataprop_outfile = new PrintStream("Data/DataProp2.txt")
   val ner = input.flatMap(coreNLP.NER(_))
   val NLPdataforclasses =ner.map(c =>
    {
     val carray = c.split(" ")
      carray(0)
    })

    //Extracting classes from the raw data using NER
    val classes =NLPdataforclasses.distinct()
    classes.collect().foreach(classes_outfile.println)

    //Extracting individuals from the raw data using NER
    val nlpdataforindividuals = ner.map( i =>
    {
      val iarray = i.split(" ")
     val y =iarray(1).mkString("").trim.filter(!broadcastStopWords.value.contains(_))
     if (!y.trim.isEmpty && y.length > 1)
     {
       iarray.mkString(",")

    }
    else
    {
      val x =" "
      x
    }})

    nlpdataforindividuals.distinct().collect().foreach(f=>
      {
        if(!f.trim.isEmpty) {
          Individuals_outfile.println(f)
        }
      })

 /*  val Ngram = input.flatMap(coreNLP.Ngram(_,2)).map(f=>f.mkString(" "))
    val z =Ngram.flatMap(coreNLP.NER(_))
    z.collect().foreach(println) */
    val hashSet1: HashSet[String] = HashSet(" ")
    val hashSetpred: HashSet[String] = HashSet(" ")
     val openie = input.map(coreNLP.openIE(_)).map(f=>f.split(":"))
      openie.collect().foreach(f=>
              {
                for (x <- f) {
                  if(!x.trim.isEmpty)
                    {
                   val openiearray =x.split(",")

                val subject =  openiearray(0).trim.replaceAll("\\s+","-").replaceAll("'", "")



                val predicate =  openiearray(1).trim.replaceAll("\\s+","-").replaceAll("'", "")
                val object1 =  openiearray(2).trim.replaceAll("\\s+","-").replaceAll("'", "")

                    val subner = coreNLP.NER(subject).mkString(" ")
                    val objner = coreNLP.NER(object1).mkString(" ")
                    if (!subner.isEmpty && !objner.isEmpty)
                      {
                        val x =  predicate.toString + "," +subner.split(" ")(0) +"," +objner.split(" ")(0) +","+"Func"
                        objprop_outfile.println(x)
                        val triplet = subject+","+predicate+","+object1+","+"Obj"
                        triplets_outfile.println(triplet)
                        //predlist_outfile.println(predicate)
                        hashSetpred += predicate

                      }
                      if(!subner.isEmpty && objner.isEmpty )
                        {
                       //   val triplet = subject+","+predicate+","+object1+","+"Data"
                       //   triplets_outfile.println(triplet)
                         // predlist_outfile.println(predicate)
                       //   hashSetpred += predicate
                       //   val dataprop = predicate +","+subner.split(" ")(0) +","+"String"
                       //   hashSet1 += dataprop
                          //Dataprop_outfile.println(dataprop)
                        }
                    }
                }
              })
    for(a <- hashSet1)
      {
        if(!a.trim.isEmpty) {
       //   Dataprop_outfile.println(a)
        }
      }
    for(b <- hashSetpred)
    {
      if(!b.trim.isEmpty) {
        predlist_outfile.println(b)
      }
    }
    objprop_outfile.close()
    classes_outfile.close()
    Individuals_outfile.close()
    triplets_outfile.close()
    predlist_outfile.close()


    //Ontology Creation
    val ONTOLOGYURI="http://www.semanticweb.org/Innovators/ontologies/2017/6/"

    val manager = OWLManager.createOWLOntologyManager
    //creating ontology manager
    val df = manager.getOWLDataFactory //In order to create objects that represent entities

    val ontology = manager.createOntology(IRI.create(ONTOLOGYURI,"Phones#"))
    //Prefix for all the entities
    val pm = new DefaultPrefixManager(null, null, ONTOLOGYURI+"Phones#")


    // Declaration Axiom for creating Classes

    val classes1=Source.fromFile("Data/classes2.txt").getLines()

    classes1.foreach(f=>{
      val cls = df.getOWLClass(f, pm)
      val declarationAxiomcls= df.getOWLDeclarationAxiom(cls)
      manager.addAxiom(ontology, declarationAxiomcls)
    })

    val objprop=Source.fromFile("Data/ObjProp2.txt").getLines()
    objprop.foreach(f=> {
      val farr=f.split(",")
      val domain = df.getOWLClass(farr(1), pm)
      val range = df.getOWLClass(farr(2), pm)
      //Creating Object property ‘hasGender’
      val objpropaxiom = df.getOWLObjectProperty(farr(0), pm)

      val rangeAxiom = df.getOWLObjectPropertyRangeAxiom(objpropaxiom, range)
      val domainAxiom = df.getOWLObjectPropertyDomainAxiom(objpropaxiom, domain)

      //Adding Axioms to ontology
      manager.addAxiom(ontology, rangeAxiom)
      manager.addAxiom(ontology, domainAxiom)
      if(farr(3)=="Func")
        manager.addAxiom(ontology, df.getOWLFunctionalObjectPropertyAxiom(objpropaxiom))
      else if(farr(3).contains("InvOf"))
      {
        val inverse=farr(3).split(":")
        val inverseaxiom = df.getOWLObjectProperty(inverse(1), pm)

        val rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverseaxiom, domain)
        val domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverseaxiom, range)

        //Adding Axioms to ontology
        manager.addAxiom(ontology, rangeAxiom)
        manager.addAxiom(ontology, domainAxiom)
        manager.addAxiom(ontology, df.getOWLInverseObjectPropertiesAxiom(objpropaxiom, inverseaxiom))
      }

    })
    val dataprop=Source.fromFile("Data/DataProp2.txt").getLines()

    dataprop.foreach(f=>{
      val farr=f.split(",")
      val domain=df.getOWLClass(farr(1),pm)
      //  Creating Data Property ‘fullName’
      val fullName = df.getOWLDataProperty(farr(0), pm)
      val domainAxiomfullName = df.getOWLDataPropertyDomainAxiom(fullName, domain)
      manager.addAxiom(ontology, domainAxiomfullName)
      if(farr(2)=="string") {
        //Defining String Datatype
        val stringDatatype = df.getStringOWLDatatype()
        val rangeAxiomfullName = df.getOWLDataPropertyRangeAxiom(fullName, stringDatatype)
        //Adding this Axiom to Ontology
        manager.addAxiom(ontology, rangeAxiomfullName)
      }
      else if(farr(2)=="int")
      {
        //Defining Integer Datatype
        val Datatype = df.getIntegerOWLDatatype()
        val rangeAxiomfullName = df.getOWLDataPropertyRangeAxiom(fullName, Datatype)
        //Adding this Axiom to Ontology
        manager.addAxiom(ontology, rangeAxiomfullName)
      }
    })

    val individuals=Source.fromFile("Data/Individuals2.txt").getLines()

    individuals.foreach(f=>{
      val farr=f.split(",")
      val cls=df.getOWLClass(farr(0), pm)
      val ind = df.getOWLNamedIndividual(farr(1), pm)
      val classAssertion = df.getOWLClassAssertionAxiom(cls, ind)
      manager.addAxiom(ontology, classAssertion)
    })

    val triplets=Source.fromFile("Data/triplets2.txt").getLines()
    triplets.foreach(f=>{
      val farr=f.split(",")
      val sub = df.getOWLNamedIndividual(farr(0), pm)

      if(farr(3)=="Obj")
      {
        val pred=df.getOWLObjectProperty(farr(1),pm)
        val obj=df.getOWLNamedIndividual(farr(2), pm)
        val objAsser = df.getOWLObjectPropertyAssertionAxiom(pred,sub, obj)
        manager.addAxiom(ontology, objAsser)
      }
      else if(farr(3)=="Data")
      {
        val pred=df.getOWLDataProperty(farr(1),pm)
        val dat=df.getOWLLiteral(farr(2))
        val datAsser = df.getOWLDataPropertyAssertionAxiom(pred,sub, dat)
        manager.addAxiom(ontology, datAsser)
      }
    })

    val os = new FileOutputStream("Data/Phonesontology.owl")
    val owlxmlFormat = new OWLXMLDocumentFormat
    manager.saveOntology(ontology, owlxmlFormat, os)
    System.out.println("Ontology Created")
    os.close()

 //  QuestionAnswer.main(List("A").toArray)

}
}
