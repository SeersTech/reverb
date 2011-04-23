package edu.washington.cs.knowitall.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

import edu.washington.cs.knowitall.extractor.HtmlSentenceExtractor;
import edu.washington.cs.knowitall.extractor.SentenceExtractor;
import edu.washington.cs.knowitall.extractor.mapper.BracketsRemover;
import edu.washington.cs.knowitall.extractor.mapper.SentenceEndFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceLengthFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceStartFilter;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;

public class DefaultObjects {
	
	private static final String tokenizerModelFile = "en-token.bin";
	private static final String taggerModelFile = "en-pos-maxent.bin";
	private static final String chunkerModelFile = "en-chunker.bin";
	private static final String sentDetectorModelFile = "en-sent.bin";
	private static final String confFunctionModelFile = "conf.weka";
	
	/** Default singleton objects */
	private static SentenceDetectorME DETECTOR;
	private static TokenizerME TOKENIZER;
	private static POSTaggerME TAGGER;
	private static ChunkerME CHUNKER;
	private static BracketsRemover BRACKETS_REMOVER;
	private static SentenceStartFilter SENTENCE_START_FILTER;
	private static SentenceEndFilter SENTENCE_END_FILTER;
	private static HtmlSentenceExtractor HTML_SENTENCE_EXTRACTOR;
	private static Classifier CONF_FUNCTION_CLASSIFIER;
	
	public static InputStream getResourceAsStream(String resource) throws IOException {
		InputStream in = DefaultObjects.class.getClassLoader().getResourceAsStream(resource);
		if (in == null) {
			throw new IOException("Couldn't load resource: " + resource);
		} else {
			return in;
		}
	}
	
	public static void initializeNlpTools() throws IOException {
		getDefaultSentenceDetector();
		getDefaultTokenizer();
		getDefaultPosTagger();
		getDefaultChunker();
	}
	
	public static Classifier getDefaultConfClassifier() throws IOException {
		if (CONF_FUNCTION_CLASSIFIER == null) {
			try {
				CONF_FUNCTION_CLASSIFIER = (Classifier)SerializationHelper.read(getResourceAsStream(confFunctionModelFile));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		return CONF_FUNCTION_CLASSIFIER;
	}
	
	public static Tokenizer getDefaultTokenizer() throws IOException {
		if (TOKENIZER == null)
			TOKENIZER =  new TokenizerME(new TokenizerModel(getResourceAsStream(tokenizerModelFile)));
		return TOKENIZER;
	}
	
	public static POSTagger getDefaultPosTagger() throws IOException {
		if (TAGGER == null)
			TAGGER = new POSTaggerME(new POSModel(getResourceAsStream(taggerModelFile)));
		return TAGGER;
	}
	
	public static Chunker getDefaultChunker() throws IOException {
		if (CHUNKER == null) 
			CHUNKER = new ChunkerME(new ChunkerModel(getResourceAsStream(chunkerModelFile)));
		return CHUNKER;
	}

	public static SentenceDetector getDefaultSentenceDetector() throws IOException {
		if (DETECTOR == null)
			DETECTOR = new SentenceDetectorME(new SentenceModel(getResourceAsStream(sentDetectorModelFile)));
		return DETECTOR;
	}
	
	public static void addDefaultSentenceFilters(SentenceExtractor extractor) {
		if (BRACKETS_REMOVER == null)
			BRACKETS_REMOVER = new BracketsRemover();
		if (SENTENCE_END_FILTER == null)
			SENTENCE_END_FILTER = new SentenceEndFilter();
		if (SENTENCE_START_FILTER == null)
			SENTENCE_START_FILTER = new SentenceStartFilter();
		extractor.addMapper(BRACKETS_REMOVER);
		extractor.addMapper(SENTENCE_END_FILTER);
		extractor.addMapper(SENTENCE_START_FILTER);
		extractor.addMapper(SentenceLengthFilter.minFilter(4));
	}
	
	public static SentenceExtractor getDefaultSentenceExtractor() throws IOException {
		SentenceExtractor extractor = new SentenceExtractor();
		addDefaultSentenceFilters(extractor);
		return extractor;
	}
	
	public static HtmlSentenceExtractor getDefaultHtmlSentenceExtractor() throws IOException {
		if (HTML_SENTENCE_EXTRACTOR == null) {
			HTML_SENTENCE_EXTRACTOR = new HtmlSentenceExtractor();
			addDefaultSentenceFilters(HTML_SENTENCE_EXTRACTOR);
		}
		return HTML_SENTENCE_EXTRACTOR;
	}
	
	public static ChunkedSentenceReader getDefaultSentenceReader(Reader in) throws IOException {
		ChunkedSentenceReader reader = new ChunkedSentenceReader(in, getDefaultSentenceExtractor());
		return reader;
	}
	
	public static ChunkedSentenceReader getDefaultSentenceReaderHtml(Reader in) throws IOException {
		ChunkedSentenceReader reader = new ChunkedSentenceReader(in, getDefaultHtmlSentenceExtractor());
		return reader;
	}

}