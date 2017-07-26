import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.simple.Document

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

/**
  * Created by shankar pentyala on 22-07-2017.
  */
object coreNLP {

  def NER(text: String): Seq[String] = {
    val props = new Properties()
    props.put("annotators","tokenize,ssplit,pos,lemma,ner,parse,dcoref")
    val pipeline = new StanfordCoreNLP(props)
    val doc = new Annotation(text)
    pipeline.annotate(doc)
    val ners = new ArrayBuffer[String]()
    val sentences = doc.get(classOf[SentencesAnnotation])
    for(sentence <- sentences;token <- sentence.get(classOf[TokensAnnotation]))
      {
        val x = token.originalText()
        if (!x.equals("") )
        {
          val ner = token.ner()
          if (ner != "O" && ner != "MISC") {
            ners += (ner + " " + token.originalText());
          }
      }}

    ners
  }

  def Ngram(text : String,gramval :Int) :Array[Array[String]] = {
    val ngrams = text.split(' ').sliding(gramval)
    val x =ngrams.toArray
    x
  }

  def openIE(text : String ) : String =
  {
    val doc1 = new Document(text)
    var triples =" "
    for(sen1 <- doc1.sentences())
      {
        triples += sen1.openie
        triples = triples.replace("),", ":")
        triples = triples.replaceAll("[ \\( \\] \\[ \\) ]", " ")
        triples += ":"
      }
triples
  }

}
