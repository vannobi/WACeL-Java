package pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredContext;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;


/**
 * Parse one fully textual scenario to one semi-structured scenario format
 * @author Edgar
 *
 */
public class ScenarioParser {
	public static final String EMPTY_CHAR = "";
	public static final String WHITESPACE_CHAR = " ";
	public static final String NON_SEQUENTIAL_GROUP_CHAR = "#";
	public static final String NEW_LINE = "\\n";
	public static final String HTML_BREAK = "<br/>";
	
	public static StructuredScenario parseToStructuredScenario(Scenario scenario) {

		//@Episódio 1: DEFINIR AS EXPRESOES REGULARES PARA IDENTIFICAR RESTRICOES, RECURSOS, ATORES, EPISODIOS(TIPOS) E EXCECOES NO CENÁRIO
				
		//@Episódio 2: INICIALIZAR O CENARIO ESTRUTURADO QUE SERÁ RETORNADO
		StructuredScenario structuredScenario = new StructuredScenario();
		structuredScenario.createScenario(scenario.getId(), scenario.getTitle(),scenario.getGoal(), scenario.getInclusionDate(), scenario.getExclusionFlag(), scenario.getId(), "scenario.getProject().getName()");
		//PROCURAR PROJETO Y ATUALIZAR

		//@Episódio 3: IDENTIFICAR O CONTEXTO, AS PRE-CONDIÕES E AS RESTRIÇÕES DEFINIDAS NO CONTEXTO DO CENÁRIO
		String contextSentence    = scenario.getContext();
		//INICIALIZAR CONTEXTO ESTRUTURADO
		StructuredContext structuredContext = new StructuredContext();
		structuredContext.createContext(EMPTY_CHAR);

		// IDENTIFICAR As LOCALIZAÇÕES GEOGRÁFICAS
		StructuredContext structuredContextTmp = new StructuredContext();
		structuredContextTmp = (StructuredContext) extractGeographicalLocations(contextSentence);
		contextSentence = ((StructuredContext) structuredContextTmp).getSentence();
		structuredContext.setGeographicalLocation(((StructuredContext) structuredContextTmp).getGeographicalLocation());

		//TBD:IDENTIFICAR AS RESTRICOES DAS LOCALIZAÇÕES GEOGRÁFICA

		// IDENTIFICAR As LOCALIZAÇÕES TEMPORALES
		structuredContextTmp = new StructuredContext();
		structuredContextTmp = (StructuredContext) extractTemporalLocations(contextSentence);
		contextSentence = ((StructuredContext) structuredContextTmp).getSentence();
		structuredContext.setTemporalLocation(((StructuredContext) structuredContextTmp).getTemporalLocation());

		//TBD:IDENTIFICAR AS RESTRICOES DAS LOCALIZAÇÕES TEMPORAL

		// IDENTIFICAR As PRE-CONDICOES
		structuredContextTmp = new StructuredContext();
		structuredContextTmp = (StructuredContext) extractPreConditions(contextSentence, structuredContextTmp);
		contextSentence = ((StructuredContext) structuredContextTmp).getSentence();
		structuredContext.setPreConditions(((StructuredContext) structuredContextTmp).getPreConditions());

		//TBD:IDENTIFICAR AS RESTRICOES DAS PRE-CONDICOES		

		// IDENTIFICAR As POST-CONDICOES
		structuredContextTmp = new StructuredContext();
		structuredContextTmp = (StructuredContext) extractPostConditions(contextSentence, structuredContextTmp);
		contextSentence = ((StructuredContext) structuredContextTmp).getSentence();
		structuredContext.setPostConditions(((StructuredContext) structuredContextTmp).getPostConditions());

		//TBD:IDENTIFICAR AS RESTRICOES DAS Post-CONDICOES		

		//LIMPAR O CONTEXTO
		contextSentence = contextSentence.replaceAll(NEW_LINE, WHITESPACE_CHAR).trim();
		structuredContext.setSentence(contextSentence);

		//ADCIONAR CONTEXTO ESTRUCTURADO A SCENARIO ESTRUCTURADO
		structuredScenario.setContext(structuredContext);

		//@Episódio 4: IDENTIFICAR OS ATORES DO CENÁRIO
		String actors = scenario.getActors();
		actors = actors.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);
		//CRIAR UMA LISTA DOS ATORES:
		String[] actorItems = actors.split(RegularExpression.REGEX_SEPARATOR_ITEMS);
		for (String actor: actorItems) {
			actor = actor.trim();
			structuredScenario.addActor(actor);
		}

		//@Episódio 5: IDENTIFICAR OS RECURSOS DO CENÁRIO  E AS RESTRIÇÕES DEFINIDAS NOS RECURSOS DO CENÁRIO
		String resources = scenario.getResources();
		resources = resources.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);
		//CRIAR UMA LISTA DOS RECURSOS 
		String[] resourceItems = resources.split(RegularExpression.REGEX_SEPARATOR_ITEMS);
		for (String resource: resourceItems) {
			resource = resource.trim();
			structuredScenario.addResource(resource);
		}
		//TBD: LISTA DE RESTRICOES PARA CADA RECURSO

		//@Episódio 6: IDENTIFICAR A LISTA DE EPISODIOS (TIPOS DE EPISODIOS) E SUAS RESTRIÇÕES DEFINIDAS NOS EPISODIOS DO CENÁRIO
		String episodes = scenario.getEpisodes();
		//IDENTIFICAR EPISODIOS:
		String[] episodesArray = episodes.split(NEW_LINE);
		for(String episode: episodesArray) {
			String episodeId = EMPTY_CHAR;
			String episodeSentence = EMPTY_CHAR;
			episode = episode.trim();
			String rawEpisode = episode;
			//CREAR EPISODIO ESTRUTURADO
			StructuredEpisode structuredEpisode = new StructuredEpisode();			
			if(!episode.isEmpty() && episode.length() > 1) {
				// IDENTIFICAR ID DE EPISODIO:
				// episodio que inicia com #: NON-SEQUENTIAL GROUP
				boolean startNonSequentialGroup = false; //FORK
				if(episode.startsWith(NON_SEQUENTIAL_GROUP_CHAR) && episode.length() > 1) {
					episode = episode.substring(1);
					startNonSequentialGroup = true;
				}
				// episodio que termina com #: NON-SEQUENTIAL GROUP
				boolean endNonSequentialGroup = false; //JOIN
				if(episode.endsWith(NON_SEQUENTIAL_GROUP_CHAR) && episode.length() > 1) {
					episode = episode.substring(0, episode.length() - 1 );
					endNonSequentialGroup = true;
				}
				//EXTRACT ID EPISODIO DA SENTENÇA
				episodeId = extractSentenceId(episode);

				//SE EPISODIO TEM ID VÁLIDO ENTAO IDENTIFICAR TIPO DE SENTENCA Y RESTRICOES
				if (episodeId != null && !episodeId.equals(EMPTY_CHAR)) {
					//CREAR ID DE EPISODIO
					episodeId = episodeId;
					//ATUALIZAR EPISODIO | EXCECAO
					episode = episode.replaceFirst(RegularExpression.REGEX_EPISODE_ID, EMPTY_CHAR);
					episode = episode.trim();

					//SE EPISODIO VALIDO ENTAO IDENTIFICAR TIPO DE SENTENCA, PRE-CONDICOES, POST-CONDICOES Y RESTRICOES
					if(!episode.isEmpty() && episode.length() > 1) {
						//VERIFICAR SE É EPISODIO OPCIONAL
						structuredEpisode = extractOptionalEpisode(episode);
						if(structuredEpisode != null && structuredEpisode.isOptional())
							episode = ((StructuredEpisode) structuredEpisode).getSentence();
						else 
							structuredEpisode = new StructuredEpisode();
						
						//VERIFICAR SE É UM EPISODIO COM UMA ACAO DE VALIDACAO COMPLICADA -> EPISODIO SIMPLE
						if(!isEpisodeWithComplicatedValidationStep(episode)) {

							//SE NAO E OPCIONAL ENTAO VERIFICAR SE É EPISODIO CONDITIONAL
							if(structuredEpisode.getType() == null || !structuredEpisode.isOptional()) {
								//CONDICIONAL EXPLICITO :  IF/WHEN <condition> THEN <sentence>
								structuredEpisode = extracExplicitConditionalEpisode(episode);
								if(structuredEpisode != null && structuredEpisode.isConditional())
									episode = structuredEpisode.getSentence();
								else 
									structuredEpisode = extractNonExplicitConditionalEpisode(episode);
								//CONDICIONAL NAO EXPLICITO : <sentence> IF/WHEN <condition>
								if(structuredEpisode != null && structuredEpisode.isConditional())
									episode = structuredEpisode.getSentence();
								else 
									structuredEpisode = new StructuredEpisode();
							}
							
						}
						//SE NAO E CONDITIONAL OU OPTIONAL ENTAO VERIFICAR SE É EPISODIO ITERATIVE DO-WHILE
						if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional()))  {
							//VERIFICAR SE É EPISODIO ITERATIVE DO-WHILE
							structuredEpisode = extractIterativeDoWhileEpisode(episode);
							if(structuredEpisode != null && structuredEpisode.isIterativeDoWhile())
								episode = ((StructuredEpisode) structuredEpisode).getSentence();
							else 
								structuredEpisode = new StructuredEpisode();

						}
						//SE NAO E CONDITIONAL OU OPTIONAL OU ITERATIVE DO-WHILE ENTAO VERIFICAR SE É EPISODIO ITERATIVE WHILE-DO
						if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional() 
																	&& structuredEpisode.isIterativeDoWhile()))  {
							//VERIFICAR SE É EPISODIO ITERATIVE WHILE-DO
							structuredEpisode = extractIterativeWhileDoEpisode(episode);
							if(structuredEpisode != null && structuredEpisode.isIterativeWhileDo())
								episode = ((StructuredEpisode) structuredEpisode).getSentence();
							else 
								structuredEpisode = new StructuredEpisode();

						}
						
						//SE NAO E CONDITIONAL OU OPTIONAL OU ITERATIVE DO-WHILE OU WHILE-DO ENTAO VERIFICAR SE É EPISODIO ITERATIVE FOR-EACH-DO
						if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional() 
																	&& structuredEpisode.isIterativeDoWhile() && !structuredEpisode.isIterativeWhileDo()))  {
							//VERIFICAR SE É EPISODIO ITERATIVE FOR-EACH-DO
							structuredEpisode = extractIterativeForEachDoEpisode(episode);
							if(structuredEpisode != null && structuredEpisode.isIterativeForEachDo())
								episode = ((StructuredEpisode) structuredEpisode).getSentence();
							else 
								structuredEpisode = new StructuredEpisode();

						}
						
						//SE NAO E CONDITIONAL OU OPTIONAL OU ITERATIVE ENTAO VERIFICAR SE É EPISODIO SIMPLE
						if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional() 
																	&& !structuredEpisode.isIterativeDoWhile() && !structuredEpisode.isIterativeWhileDo()
																	 && !structuredEpisode.isIterativeForEachDo()))  {
							//Incializar Episodio Estructurado
							episodeSentence = episode;
							structuredEpisode.createSimpleEpisode(episodeId, episodeSentence);
						}


						//TRATAR AS RESTRICOES DO EPISODIO (1. episodio 1. Restricao: Rest1, rest 2.)
						StructuredEpisode structuredEpisodeTmp = new StructuredEpisode();
						structuredEpisodeTmp = (StructuredEpisode) extractExplicitConstraints(episode, structuredEpisodeTmp);
						episode = ((StructuredEpisode) structuredEpisodeTmp).getSentence();
						/*
						if(((StructuredEpisode) structuredEpisodeTmp).getConstraints() == null|| ((StructuredEpisode) structuredEpisodeTmp).getConstraints().isEmpty())
							structuredEpisodeTmp = (StructuredEpisode) extractNonExplicitConstraints(episode, structuredEpisodeTmp);
							*/
						episode = ((StructuredEpisode) structuredEpisodeTmp).getSentence();
						structuredEpisode.setConstraints(((StructuredEpisode) structuredEpisodeTmp).getConstraints());

						//TRATAR AS PRÉ-CONDIÇÕES DO EPISODIO (1. episodio 1. Pré-condição: pre-condicao1, pre-condicao2.)
						structuredEpisodeTmp = new StructuredEpisode();
						structuredEpisodeTmp = (StructuredEpisode) extractPreConditions(episode, structuredEpisodeTmp);
						episode = ((StructuredEpisode) structuredEpisodeTmp).getSentence();
						structuredEpisode.setPreConditions(((StructuredEpisode) structuredEpisodeTmp).getPreConditions());

						//TRATAR AS PÓS-CONDIÇÕES DO EPISODIO (1. episodio 1. Pos-condição: pos-condicao1, pos-condicao2.)
						structuredEpisodeTmp = new StructuredEpisode();
						structuredEpisodeTmp = (StructuredEpisode) extractPostConditions(episode, structuredEpisodeTmp);
						episode = ((StructuredEpisode) structuredEpisodeTmp).getSentence();
						structuredEpisode.setPostConditions(((StructuredEpisode) structuredEpisodeTmp).getPostConditions());

						//REMOVE PUNCTUATION AT THE END AND THE BEGINOF THE SENTENCE
						//episode = episode.replaceAll(RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_AND_END_SENTENCE, EMPTY_CHAR);
						episode = episode.trim();
						
						//ATUALIZAR EPISODIO STRUTURADO: SENTENCA, ID
						//restaurar FORK
						if(startNonSequentialGroup)
							episode = NON_SEQUENTIAL_GROUP_CHAR + episode ;
						//restaurar JOIN
						if(endNonSequentialGroup)
							episode = episode + NON_SEQUENTIAL_GROUP_CHAR ;
						
						structuredEpisode.setSentence(episode);
						structuredEpisode.setId(episodeId);
						//ADICIONAR EPISODIO NA LISTA DE EPISODIOS STRUTURADOS
						structuredEpisode.setRawEpisode(rawEpisode);
						structuredScenario.addEpisode(structuredEpisode);

					}
					//SENTENÇA NAO VALIDA ou VAZIA
					else {
						//Incializar Episodio Estructurado
						episodeSentence = episode;
						structuredEpisode.createSimpleEpisode(episodeId, episodeSentence);
						//ADICIONAR EPISODIO NA LISTA DE EPISODIOS STRUTURADOS
						structuredEpisode.setRawEpisode(rawEpisode);
						structuredScenario.addEpisode(structuredEpisode);
					}
				}
				//TRATAR EPISODIOS SEM ID OU POSSIVEIS SENTENCA, RESTRICOES, PRE-CONDICOES OU POST-CONDICOES DO EPISODIO ANTERIOR
				else {
					//RECUPERAR POST-CONDICOES DA SENTENCA
					StructuredEpisode structuredEpisodeTmpPost = new StructuredEpisode();
					structuredEpisodeTmpPost = (StructuredEpisode) extractPostConditions(episode, structuredEpisodeTmpPost);
					episode = structuredEpisodeTmpPost.getSentence();
					
					//RECUPERAR PRE-CONDICOES DA SENTENCA
					StructuredEpisode structuredEpisodeTmpPre = new StructuredEpisode();
					structuredEpisodeTmpPre = (StructuredEpisode) extractPreConditions(episode, structuredEpisodeTmpPre);
					episode = structuredEpisodeTmpPre.getSentence();
																									
					//RECUPERAR RESTRICOES DA SENTENCA
					StructuredEpisode structuredEpisodeTmpConstraint = new StructuredEpisode();
					structuredEpisodeTmpConstraint = (StructuredEpisode) extractExplicitConstraints(episode, structuredEpisodeTmpConstraint);
					episode = structuredEpisodeTmpConstraint.getSentence();
					/*
					if(((StructuredEpisode) structuredEpisodeTmpConstraint).getConstraints() == null|| ((StructuredEpisode) structuredEpisodeTmpConstraint).getConstraints().isEmpty())
						structuredEpisodeTmpConstraint = (StructuredEpisode) extractNonExplicitConstraints(episode, structuredEpisodeTmpConstraint);
					*/
					episode = ((StructuredEpisode) structuredEpisodeTmpConstraint).getSentence();
					
					//REMOVE PUNCTUATION AT THE END AND THE BEGIN OF THE SENTENCE
					//episode = episode.replaceAll(RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_AND_END_SENTENCE, EMPTY_CHAR);
					episode = episode.trim();
										
					/*POSSIVEL SENTENCA  DO EPISODIO CONDICIONAL ANTERIOR (1. IF condicao = OK THEN \n
																			executar passo) */
					/*POSSIVEL CONDICIOes DO EPISODIO LOOP ANTERIOR (1. DO action WHILE \n
																				condicao = OK) */
					/*POSSIVEL NOVO EPISODIO SEM ID (DO action WHILE A OR B*/
					if(!episode.isEmpty()) {
						if(structuredScenario.getEpisodes() != null && structuredScenario.getEpisodes().size() > 0) {
							//RECUPERAR EPISODIO ANTERIOR
							int lastEpisode = structuredScenario.getEpisodes().size() - 1;
							StructuredEpisode structuredEpisodePrev = structuredScenario.getEpisodes().get(lastEpisode);
							//SE EPISODIO ANTERIOR 'E CONDICIONAL E SENTENCA EPISODIO ANTERIOR 'E VAZIA ENTAO: ADICIONAR SENTENCA E ... 
							if(structuredEpisodePrev.isConditional() && (structuredEpisodePrev.getSentence() == null || structuredEpisodePrev.getSentence().isEmpty())) {
								structuredEpisodePrev.setSentence(episode);
								if(structuredEpisodeTmpPost.getPostConditions().size() > 0)
									structuredEpisodePrev.getPostConditions().addAll(structuredEpisodeTmpPost.getPostConditions());
								if(structuredEpisodeTmpPost.getPreConditions().size() > 0)
									structuredEpisodePrev.getPreConditions().addAll(structuredEpisodeTmpPre.getPreConditions());
								if(structuredEpisodeTmpPost.getConstraints().size() > 0)
									structuredEpisodePrev.getConstraints().addAll(structuredEpisodeTmpConstraint.getConstraints());
								//ATUALIZAR EPISODIO NA LISTA DE EPISODIOS STRUTURADaS
								structuredScenario.getEpisodes().set(lastEpisode, structuredEpisodePrev);
							}
							//SE EPISODIO ANTERIOR 'E LOOP E CONDICAO EPISODIO ANTERIOR 'E VAZIA ENTAO: ADICIONAR CONDICOES E ...
							else if(structuredEpisodePrev.isIterativeDoWhile() && (structuredEpisodePrev.getConditions() == null || structuredEpisodePrev.getConditions().isEmpty())) {
								//ADICOONAR CONDICOES DO LOOP
								String[] conditions = episode.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
								for (String condition: conditions) {
									condition = condition.trim();
									structuredEpisodePrev.addCondition(condition);
								}
								if(structuredEpisodeTmpPost.getPostConditions().size() > 0)
									structuredEpisodePrev.getPostConditions().addAll(structuredEpisodeTmpPost.getPostConditions());
								if(structuredEpisodeTmpPost.getPreConditions().size() > 0)
									structuredEpisodePrev.getPreConditions().addAll(structuredEpisodeTmpPre.getPreConditions());
								if(structuredEpisodeTmpPost.getConstraints().size() > 0)
									structuredEpisodePrev.getConstraints().addAll(structuredEpisodeTmpConstraint.getConstraints());
								//ATUALIZAR EPISODIO NA LISTA DE EPISODIOS STRUTURADaS
								structuredScenario.getEpisodes().set(lastEpisode, structuredEpisodePrev);
								
							}
							//NOVO EPISODIO SEM ID
							else {
								//VERIFICAR SE É EPISODIO OPCIONAL
								structuredEpisode = extractOptionalEpisode(episode);
								if(structuredEpisode != null && structuredEpisode.isOptional())
									episode = ((StructuredEpisode) structuredEpisode).getSentence();
								else 
									structuredEpisode = new StructuredEpisode();
								
								//VERIFICAR SE É UM EPISODIO COM UMA ACAO DE VALIDACAO COMPLICADA -> EPISODIO SIMPLE
								if(!isEpisodeWithComplicatedValidationStep(episode)) {

									//SE NAO E OPCIONAL ENTAO VERIFICAR SE É EPISODIO CONDITIONAL
									if(structuredEpisode.getType() == null || !structuredEpisode.isOptional()) {
										//CONDICIONAL EXPLICITO :  IF/WHEN <condition> THEN <sentence>
										structuredEpisode = extracExplicitConditionalEpisode(episode);
										if(structuredEpisode != null && structuredEpisode.isConditional())
											episode = structuredEpisode.getSentence();
										else 
											structuredEpisode = extractNonExplicitConditionalEpisode(episode);
										//CONDICIONAL NAO EXPLICITO : <sentence> IF/WHEN <condition>
										if(structuredEpisode != null && structuredEpisode.isConditional())
											episode = structuredEpisode.getSentence();
										else 
											structuredEpisode = new StructuredEpisode();
									}
								}
								
								//SE NAO E CONDITIONAL E OPTIONAL ENTAO VERIFICAR SE É EPISODIO LOOP
								if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional()))  {
									//VERIFICAR SE É EPISODIO LOOP
									structuredEpisode = extractIterativeDoWhileEpisode(episode);
									if(structuredEpisode != null && structuredEpisode.isIterativeDoWhile())
										episode = ((StructuredEpisode) structuredEpisode).getSentence();
									else 
										structuredEpisode = new StructuredEpisode();

								}

								//SE NAO E CONDITIONAL E OPTIONAL E LOOP ENTAO VERIFICAR SE É EPISODIO SIMPLE
								if(structuredEpisode.getType() == null || (!structuredEpisode.isConditional() && !structuredEpisode.isOptional() && !structuredEpisode.isIterativeDoWhile()))  {
									//Incializar Episodio Estructurado
									episodeSentence = episode;
									structuredEpisode.createSimpleEpisode(episodeId, episodeSentence);
								}


								//TRATAR AS RESTRICOES DO EPISODIO (1. episodio 1. Restricao: Rest1, rest 2.)
								if(structuredEpisodeTmpPost.getConstraints().size() > 0)
									structuredEpisodePrev.getConstraints().addAll(structuredEpisodeTmpConstraint.getConstraints());
								
								//TRATAR AS PRÉ-CONDIÇÕES DO EPISODIO (1. episodio 1. Pré-condição: pre-condicao1, pre-condicao2.)
								if(structuredEpisodeTmpPost.getPreConditions().size() > 0)
									structuredEpisodePrev.getPreConditions().addAll(structuredEpisodeTmpPre.getPreConditions());
								
								//TRATAR AS PÓS-CONDIÇÕES DO EPISODIO (1. episodio 1. Pos-condição: pos-condicao1, pos-condicao2.)
								if(structuredEpisodeTmpPost.getPostConditions().size() > 0)
									structuredEpisodePrev.getPostConditions().addAll(structuredEpisodeTmpPost.getPostConditions());


								//REMOVE PUNCTUATION AT THE END AND THE BEGINOF THE SENTENCE
								//episode = episode.replaceAll(RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_AND_END_SENTENCE, EMPTY_CHAR);
								episode = episode.trim();
								
								//ATUALIZAR EPISODIO STRUTURADO: SENTENCA, ID
								//restaurar FORK
								if(startNonSequentialGroup)
									episode = NON_SEQUENTIAL_GROUP_CHAR + episode ;
								//restaurar JOIN
								if(endNonSequentialGroup)
									episode = episode + NON_SEQUENTIAL_GROUP_CHAR ;
								
								structuredEpisode.setSentence(episode);
								structuredEpisode.setId(EMPTY_CHAR);
								//ADICIONAR EPISODIO NA LISTA DE EPISODIOS STRUTURADOS
								structuredEpisode.setRawEpisode(rawEpisode);
								structuredScenario.addEpisode(structuredEpisode);

							}
														
						}
						
					} 
					//RESTRICOES, PRE-CONDICOES OU POST-CONDICOES DO EPISODIO ANTERIOR
					else {
						if(structuredScenario.getEpisodes() != null && structuredScenario.getEpisodes().size() > 0) {
							//RECUPERAR EPISODIO ANTERIOR
							int lastEpisode = structuredScenario.getEpisodes().size() - 1;
							StructuredEpisode structuredEpisodePrev = structuredScenario.getEpisodes().get(lastEpisode);

							if(structuredEpisodeTmpPost.getPostConditions().size() > 0)
								structuredEpisodePrev.getPostConditions().addAll(structuredEpisodeTmpPost.getPostConditions());
							if(structuredEpisodeTmpPre.getPreConditions().size() > 0)
								structuredEpisodePrev.getPreConditions().addAll(structuredEpisodeTmpPre.getPreConditions());
							if(structuredEpisodeTmpConstraint.getConstraints().size() > 0)
								structuredEpisodePrev.getConstraints().addAll(structuredEpisodeTmpConstraint.getConstraints());
							//ATUALIZAR EPISODIO NA LISTA DE EPISODIOS STRUTURADaS
							structuredScenario.getEpisodes().set(lastEpisode, structuredEpisodePrev);

						}
					}
										
				}

			}
		}
		//--@Episódio 7: IDENTIFICAR AS ALTERNATIVES/EXCEÇÕES(CAUSA ->SOLUCAO) DO CENÁRIO
		String alternatives = scenario.getAlternative();
		//IDENTIFICAR ALTERNATIVES/EXCEÇÕES:
		String[] alternativesArray = alternatives.split(NEW_LINE);
		StructuredAlternative prevAlternative = null;//Pointer to previous alternative
		int lastAlternative = -1;
		for(String alternative: alternativesArray) {
			String alternativeId = EMPTY_CHAR;
			String alternativeSolution = EMPTY_CHAR;
			alternative = alternative.trim();
			String rawAlternative = alternative;
			//CREAR EXCECAO ESTRUTURADA
			StructuredAlternative currentStructAlternative = new StructuredAlternative();
			if(!alternative.isEmpty() && alternative.length() > 1) {
				// IDENTIFICAR ID DE ALTERNATIVE/EXCECAO:
				//EXTRACT ID ALTERNATIVE/EXCECAO DA SENTENÇA
				alternativeId = extractSentenceId(alternative);

				//SE EXCECAO TEM ID VÁLIDO ENTAO IDENTIFICAR CAUSA E SOLUCAO
				if (alternativeId != null && !alternativeId.equals(EMPTY_CHAR)) {
					//CREAR ID DE EXCECAO
					
					//IDENTIFICAR O EPISODIO QUE ORIGINOU A ALTERNATIVE/EXCECAO
					//TAMBEM PODE SER UM STEP (DA SOLUCAO) DA ALTERNATIVA /EXCECAO ANTERIOR
					/*
					 * episodio ID: 1 , 2 , 3
					 * alternative ID: 1.1 , 1.a , 1a
					 * alternative solution ID: 1, a , 1.1.1 , 1.a.1 , 1a.1 , 1a1
					 */
					//AQUELE EPISODIO QUE TEM O ID IGUAL AO INICIO DO ID DA ALTERNATIVE/EXCECAO, EX. 1.1 EXCECAO ---> 1. EPISODIO 
					//SE TEM 2 PARTES <ID><REF> ENTAO É UMA NOVA ALTERNATIVA
					//SE TEM 3 PARTES <ID><REF><REF> ENTAO É UM PASSO DA ALTERNATIVA ANTERIOR
					//SE TEM 1 PARTE <ID> | <REF> ENTAO É UM PASSO DA ALTERNATIVA ANTERIOR
					//LIMPAR O ID-ALTERNATIVE DE ", ; . %s" FINAL o INICIO
					alternativeId = alternativeId.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT, EMPTY_CHAR);
					alternativeId = alternativeId.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT, EMPTY_CHAR);
					//Identificar partes do Id
					
					Matcher matcherGroupsFromId = RegularExpression.PATTERN_ALTERNATIVE_ID.matcher(alternativeId);
					Boolean isNewAlternative = false;
					Boolean isStepOfPreviousAlternative = false;
					StructuredEpisode branchingEpisode = null;
					
					if (matcherGroupsFromId.matches()) {
						//Alternative: ID parts
						String id = matcherGroupsFromId.group(2); //episode id
						String ref = matcherGroupsFromId.group(3); //alternative id
						String refRef = matcherGroupsFromId.group(4); //alternative solution step
						//ID principal de ALTERNATIVE/EXCECAO
						if (id != null && !id.isEmpty() && ref != null && !ref.isEmpty() && (refRef == null || refRef.isEmpty()) ) {//2 partes
							isNewAlternative = true;
							for (StructuredEpisode structuredEpisode : structuredScenario.getEpisodes()) {
								if (structuredEpisode.getId() != null && !structuredEpisode.getId().isEmpty()) {
									String episodeIdCandidate = structuredEpisode.getId();
									//LIMPAR O ID-EPISODIO DE ", ; . %s" AL FINAL 
									episodeIdCandidate = episodeIdCandidate.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT, EMPTY_CHAR);
									if (id.equals(episodeIdCandidate)) {
										//Asignar episodio a alternativa
										branchingEpisode = structuredEpisode;
										break;
									}
								}
							}
						} 
						//ID de paso-step-solucao de ALTERNATIVE/EXCECAO
						else if ((id != null && !id.isEmpty() && ref != null && !ref.isEmpty() && refRef != null && !refRef.isEmpty()) || (id != null && !id.isEmpty())) { //1 o 3 partes
							if(structuredScenario.getAlternative() != null && structuredScenario.getAlternative().size() > 0) {
								//RECUPERAR ALTERNATIVE/EXCECAO ANTERIOR
								lastAlternative = structuredScenario.getAlternative().size() - 1;
								prevAlternative = structuredScenario.getAlternative().get(lastAlternative);
								if(prevAlternative != null) 
									if((prevAlternative.getSolution() == null || prevAlternative.getSolution().isEmpty()) || prevAlternative.getPostConditions().isEmpty())
										isStepOfPreviousAlternative = true;
									else
										isNewAlternative = true;
							}
						}					
						//New ALTERNATIVE/EXCECAO 
						else {
							isNewAlternative = true;
						}	   
					}
					
									
					
					//ATUALIZAR ALTERNATIVE/EXCECAO
					alternative = alternative.replaceFirst(RegularExpression.REGEX_ALTERNATIVE_ID, EMPTY_CHAR);
					alternative = alternative.trim();

					//SE ALTERNATIVE/EXCECAO VALIDA ENTAO IDENTIFICAR CAUSAS/SOLUCAO
					if(!alternative.isEmpty() ) {// && alternative.length() > 1) {
						//TRATAR AS PÓS-CONDIÇÕES DA ALTERNATIVE/EXCECAO
						StructuredAlternative structuredAlternativeTmpPost = new StructuredAlternative();
						structuredAlternativeTmpPost = (StructuredAlternative) extractPostConditions(alternative, structuredAlternativeTmpPost);
						alternative = structuredAlternativeTmpPost.getSolution().get(0);

						if(isNewAlternative)	{					
							//VERIFICAR FORMATO IF-THEN DE ALTERNATE/EXCECAO
							currentStructAlternative = extractCauseAlternativeIfThenFormat(alternative);
							if(currentStructAlternative != null) {
								//ALTERNATIVE/EXCECAO (1.1. IF causa THEN solution. Pos-condição: pos-condicao1, pos-condicao2.)
								if(currentStructAlternative.getSolution() != null && !currentStructAlternative.getSolution().isEmpty()) {
									//ATUALIZAR EXCECAO STRUTURADA: SOLUCAO, ID
									currentStructAlternative.setId(alternativeId);								
								} else {	//ALTERNATIVE/EXCECAO (1.1. IF causa THEN)
									//ATUALIZAR EXCECAO STRUTURADA: SOLUCAO, ID
									structuredAlternativeTmpPost.setCauses(currentStructAlternative.getCauses());
									currentStructAlternative = new StructuredAlternative();
									//Incializar EXCECAO EstructuradA
									currentStructAlternative.createAlternative(alternativeId); 
									currentStructAlternative.setCauses(structuredAlternativeTmpPost.getCauses());								
								}
								currentStructAlternative.setIfThenFormat(true);
							} else {
								//VERIFICAR FORMATO EM VARIAS LINHAS: <STEP><REFERENCE> <CAUSA>
								//									                    <SOLUCAO>	
								//		        							            <POST-CONDICAO>	
								currentStructAlternative = new StructuredAlternative();
								//Incializar EXCECAO EstructuradA
								currentStructAlternative.createAlternative(alternativeId);
								//LIMPAR a CAUSAS DE ", ; . %s" AL FINAL
								alternative = alternative.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
								//CRIAR UMA LISTA DE CAUSAS: 
								String[] causes = alternative.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
								for (String cause: causes) {
									cause = cause.trim();
									currentStructAlternative.addCause(cause);
								}
								//Solution is in next alternative-line							
							}
							//ADICIONAR POST-CONDICOES
							currentStructAlternative.setPostConditions(((StructuredAlternative) structuredAlternativeTmpPost).getPostConditions());
							//ADICIONAR Excecao NA LISTA DE Excecoes STRUTURADaS
							currentStructAlternative.setRawAlternative(rawAlternative);
							
							structuredScenario.addAlternative(currentStructAlternative);

							//SET EPISODIO
							if(branchingEpisode != null) {
								currentStructAlternative.setBranchingEpisode(branchingEpisode);
							}
							prevAlternative = currentStructAlternative;//Pointer to previous alternative	
						} else {
							//SE ALTERNATIVE/EXCECAO ANTERIOR NAO TEM SOLUCAO VAZIA E NAO TEM POST-CONDICOES ENTAO: ADICIONAR SOLUCAO E ... 
							if(prevAlternative != null) {
								prevAlternative.addSolution(alternative);
								if(structuredAlternativeTmpPost.getPostConditions().size() > 0)
									prevAlternative.getPostConditions().addAll(structuredAlternativeTmpPost.getPostConditions());
								//ATUALIZAR ALTERNATIVE/EXCECAO NA LISTA DE EXCECOES STRUTURADaS
								structuredScenario.getAlternative().set(lastAlternative, prevAlternative);
							}
						}
					}										
					
				}
				//TRATAR EXCECAO SEM ID OU POSSIVEIS SOLUCAO OU POST-CONDICOES DA EXCECAO ANTERIOR
				else {
					
					//VERIFICAR FORMATO IF-THEN DE ALTERNATIVE/EXCECAO
					currentStructAlternative = extractCauseAlternativeIfThenFormat(alternative);
					if(currentStructAlternative != null) {
						//TRATAR AS PÓS-CONDIÇÕES DA ALTERNATIVE/EXCECAO
						StructuredAlternative structuredAlternativePostTmp = new StructuredAlternative();
						structuredAlternativePostTmp = (StructuredAlternative) extractPostConditions(alternative, structuredAlternativePostTmp);
						alternative = structuredAlternativePostTmp.getSolution().get(0);
						//ALTERNATIVE/EXCECAO (1.1. IF causa THEN solution. Pos-condição: pos-condicao1, pos-condicao2.)
						if(currentStructAlternative.getSolution() != null && !currentStructAlternative.getSolution().isEmpty()) {
							//ATUALIZAR EXCECAO STRUTURADA: SOLUCAO, ID
							currentStructAlternative.setId(alternativeId);								
						} else {	//ALTERNATIVE/EXCECAO (1.1. IF causa THEN)
							//ATUALIZAR EXCECAO STRUTURADA: SOLUCAO, ID
							structuredAlternativePostTmp.setCauses(currentStructAlternative.getCauses());
							currentStructAlternative = new StructuredAlternative();
							//Incializar EXCECAO EstructuradA
							currentStructAlternative.createAlternative(alternativeId); 
							currentStructAlternative.setCauses(structuredAlternativePostTmp.getCauses());	
							
						}
						//ADICIONAR POST-CONDICOES
						currentStructAlternative.setPostConditions(((StructuredAlternative) structuredAlternativePostTmp).getPostConditions());
						//ADICIONAR Excecao NA LISTA DE Excecoes STRUTURADaS
						currentStructAlternative.setRawAlternative(rawAlternative);
						currentStructAlternative.setIfThenFormat(true);
						
						structuredScenario.addAlternative(currentStructAlternative);

					} else {
						//RECUPERAR POST-CONDICOES
						StructuredAlternative structuredAlternativeTmpPost = new StructuredAlternative();
						structuredAlternativeTmpPost = (StructuredAlternative) extractPostConditions(alternative, structuredAlternativeTmpPost);
						alternative =  structuredAlternativeTmpPost.getSolution().get(0);//First sentence (Scenario [Leite] has only one solution sentence)

						//REMOVE PUNCTUATION AT THE END AND THE BEGIN OF THE SENTENCE
						//alternative = alternative.replaceAll(RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_AND_END_SENTENCE, EMPTY_CHAR);
						alternative = alternative.trim();
						/*POSSIVEL SOLUCAO  DA ALTERNATIVE/EXCECAO ANTERIOR (1.1 IF cause = OK THEN \n
																				executar passo) */
								/*POSSIVEL NOVA ALTERNATIVE/EXCECAO SEM ID (IF cause THEN solution */
						if(!alternative.isEmpty()) {
							//SE ALTERNATIVES/EXCECOES NAO VAZIA
							if(structuredScenario.getAlternative() != null && structuredScenario.getAlternative().size() > 0) {
								//RECUPERAR ALTERNATIVE/EXCECAO ANTERIOR
								lastAlternative = structuredScenario.getAlternative().size() - 1;
								prevAlternative = structuredScenario.getAlternative().get(lastAlternative);
								//SE ALTERNATIVE/EXCECAO ANTERIOR NAO TEM SOLUCAO  OU NAO TEM POST-CONDICOES ENTAO: ADICIONAR SOLUCAO E ... 
								if((prevAlternative.getSolution() == null || prevAlternative.getSolution().isEmpty()) || prevAlternative.getPostConditions().isEmpty()) {
									prevAlternative.addSolution(alternative);
									if(structuredAlternativeTmpPost.getPostConditions().size() > 0)
										prevAlternative.getPostConditions().addAll(structuredAlternativeTmpPost.getPostConditions());
									//ATUALIZAR ALTERNATIVE/EXCECAO NA LISTA DE EXCECOES STRUTURADaS
									structuredScenario.getAlternative().set(lastAlternative, prevAlternative);
								}
								//NOVA ALTERNATIVE/EXCECAO SEM ID
								else {
									//CREAR EXCECAO STRUTURADA: SOLUCAO, ID, POST-CONDICOES

									//VERIFICAR SE TEM CAUSA: CONDICOES
									currentStructAlternative = extractCauseAlternativeIfThenFormat(alternative);
									if(currentStructAlternative != null) {
										currentStructAlternative.setIfThenFormat(true);
										alternative = currentStructAlternative.getSolution().get(0);//First sentence (Scenario [Leite] has only one solution sentence)
									} else {
										//SE NAO TEM FORMATO DE EXCECAO
										//EXCECAO COM FORMATO EM VARIAS LINHAS: <STEP><REFERENCE> <CAUSA>
										//									                    <SOLUCAO>	
										//	
										currentStructAlternative = new StructuredAlternative();
										currentStructAlternative.createAlternative(null);
										//Incializar EXCECAO EstructuradA
										//LIMPAR a CAUSAS DE ", ; . %s" AL FINAL 
										alternative = alternative.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
										//CRIAR UMA LISTA DE CAUSAS: 
										String[] causes = alternative.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
										for (String cause: causes) {
											cause = cause.trim();
											currentStructAlternative.addCause(cause);
										}
										//Solution is in next alternative-line	
									}

									//ADICIONAR POST-CONDICOES (IDENTIFICADAS NO PASO: POSSIVEL POST-CONDICAO DA EXCECAO ANTERIOR)
									currentStructAlternative.setPostConditions(structuredAlternativeTmpPost.getPostConditions());
									//ADICIONAR Excecao NA LISTA DE Excecoes STRUTURADaS
									currentStructAlternative.setRawAlternative(rawAlternative);
									
									structuredScenario.addAlternative(currentStructAlternative);
								}
															
							} else {//NOVA EXCECAO
								//EXCECAO COM FORMATO EM VARIAS LINHAS: <STEP><REFERENCE> <CAUSA>
								//									                    <SOLUCAO>	
								//		        							            <POST-CONDICAO>	
								currentStructAlternative = new StructuredAlternative();
								//Incializar EXCECAO EstructuradA
								currentStructAlternative.createAlternative(null);
								//LIMPAR a CAUSAS DE ", ; . %s" AL FINAL 
								alternative = alternative.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
								//CRIAR UMA LISTA DE CAUSAS: 
								String[] causes = alternative.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
								for (String cause: causes) {
									cause = cause.trim();
									currentStructAlternative.addCause(cause);
								}
								//ADICIONAR POST-CONDICOES (IDENTIFICADAS NO PASO: POSSIVEL POST-CONDICAO DA EXCECAO ANTERIOR)
								currentStructAlternative.setPostConditions(structuredAlternativeTmpPost.getPostConditions());
								//Solution is in next alternative-line							

								//ADICIONAR Excecao NA LISTA DE Excecoes STRUTURADaS
								currentStructAlternative.setRawAlternative(rawAlternative);
								
								structuredScenario.addAlternative(currentStructAlternative);
							}
							
						} 
						//POST-CONDICOES DA ALTERNATIVE/EXCECAO ANTERIOR
						else {
							if(structuredScenario.getAlternative() != null && structuredScenario.getAlternative().size() > 0) {
								//RECUPERAR ALTERNATIVE/EXCECAO ANTERIOR
								lastAlternative = structuredScenario.getAlternative().size() - 1;
								currentStructAlternative = structuredScenario.getAlternative().get(lastAlternative);
								//ADICIONAR POST-CONDICOES
								currentStructAlternative.getPostConditions().addAll(structuredAlternativeTmpPost.getPostConditions());
								//ADICIONAR ALTERNATIVE/Excecao NA LISTA DE ALTERNATIVES/Excecoes STRUTURADaS
								structuredScenario.getAlternative().set(lastAlternative, currentStructAlternative);
							}
						}
						
					}
				}
				
				
			}
		}
		
		//--@Episódio 8: IDENTIFICAR AS ALTERNATIVES DO CENÁRIO QUE TEM GO_TO A ALGUM EPISODIO OU FINALIZAM O SCENARIO
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			int solutionStep = 1;
			for(String solution : alternative.getSolution() ) {
				//Get Id/Step of episode after GO_TO 
				Matcher matcherStepFromGoTo = RegularExpression.PATTERN_ALTERNATE_GO_TO.matcher(solution.toLowerCase());
				if (matcherStepFromGoTo.matches()) {
				    String episodeId = matcherStepFromGoTo.group(5);
				    //Set solution step with GO_TO struct
				    alternative.setSolutionStepWithGoToEpisode(solutionStep);
				    //Set episode after GO_TO struct
				    StructuredEpisode goToEpisode = null;
					if (episodeId != null && !episodeId.isEmpty() ) {
						for (StructuredEpisode structuredEpisode : structuredScenario.getEpisodes()) {
							if (structuredEpisode.getId() != null && !structuredEpisode.getId().isEmpty()) {
								String episodeIdCandidate = structuredEpisode.getId();
								//LIMPAR O ID-EPISODIO DE ", ; . %s" AL FINAL 
								episodeIdCandidate = episodeIdCandidate.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT, EMPTY_CHAR);
								if (episodeId.equals(episodeIdCandidate)) {
									//Asignar episodio a alternativa
									goToEpisode = structuredEpisode;
									alternative.setGoToEpisode(goToEpisode);
									
									break;
								}
							}
						}
					} 				   
				}
				else {
					//System or Use Case or Scenario ends or terminates or finishes
					if(solution.toLowerCase().matches(RegularExpression.REGEX_ALTERNATIVE_ENDS)) {
						alternative.setScenarioFinish(true);
					}
				}
				solutionStep++;
			}
			//FIX: Alternative without solution but with cause
			if((alternative.getSolution() == null || alternative.getSolution().isEmpty()) && !alternative.isIfThenFormat()) {
				alternative.setSolution(alternative.getCauses());
				alternative.setCauses(new ArrayList<String>());
			}
		}
		
		return structuredScenario;
	}

	/**
	 * Return a temporal [StructuredEpisode | StructuredContext] with: PreConditions and an updated sentence (FREE of PreConditions)
	 * @example "Episode 1. Pre-Condition: pre1 AND pre2." --> sentence = "Episode 1", preConditions = {"pre1", "pre2"} 
	 * @param sentence
	 * @return
	 */
	private static Object extractPreConditions(String sentence, Object returnObject) {
		if(returnObject instanceof StructuredEpisode) {
			//Episodio para retornar unsa sentencia actualizada y sus pre-condiciones
			returnObject = new StructuredEpisode();
			//Inicializar episodio
			((StructuredEpisode) returnObject).createSimpleEpisode(null, sentence);
		} else if (returnObject instanceof StructuredContext) {
			//Episodio para retornar unsa sentencia actualizada y sus pre-condiciones
			returnObject = new StructuredContext();
			//Inicializar episodio
			((StructuredContext) returnObject).createContext(sentence);
		} else {
			return null;
		}

		// PROCURAR POR: PRE-CONDICOES:
		
		Matcher matcherPreCondSentence = RegularExpression.PATTERN_LEFT_SIDE_PRE_COND.matcher(sentence);
		int count = 0;
		int startPreCondSentenceLabel = -1;
		int endPreCondSentenceLabel = -1;
		int startPreCondSentenceItems = -1;
		int endPreCondSentenceItems = -1;
		while(matcherPreCondSentence.find()) {
			count++;
			startPreCondSentenceLabel = matcherPreCondSentence.start();
			endPreCondSentenceLabel = matcherPreCondSentence.end();
			break;//firs occurrence
		}

		if (startPreCondSentenceLabel >= 0 && endPreCondSentenceLabel > 0) {
			String sentencePreCondItems = sentence.substring(endPreCondSentenceLabel);
			//Find first occurrence of "\. | \n" (end of PreCond. items)
			
			Matcher matcherEndPreCondItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentencePreCondItems);
			while(matcherEndPreCondItems.find()) {
				count++;
				startPreCondSentenceItems = 0;
				endPreCondSentenceItems = matcherEndPreCondItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE PRE-CONDICOES
			if(startPreCondSentenceItems >= 0 && endPreCondSentenceItems > 0) {
				sentencePreCondItems = sentencePreCondItems.substring(startPreCondSentenceItems,endPreCondSentenceItems);
				//ATUALIZAR O EPISODIO COM PRE-CONDICOES
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_PRE_CONDITION, WHITESPACE_CHAR);
				sentence = sentence.replace(sentencePreCondItems, WHITESPACE_CHAR);
				sentencePreCondItems = sentencePreCondItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);
				String[] items = sentencePreCondItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String condition: items) {
					condition = condition.trim();
					if(returnObject instanceof StructuredEpisode) {
						((StructuredEpisode) returnObject).addPreCondition(condition);
					} else if (returnObject instanceof StructuredContext) {
						((StructuredContext) returnObject).addPreCondition(condition);

					}
				}
				if(returnObject instanceof StructuredEpisode) {
					((StructuredEpisode) returnObject).setSentence(sentence);
				} else if (returnObject instanceof StructuredContext) {
					((StructuredContext) returnObject).setSentence(sentence);
				}

			}
		}
		return returnObject;
	}

	/**
	 * Return a temporal [StructuredEpisode | StructuredContext | StructuredAlternative] with: PostConditions and an updated sentence (FREE of PostConditions)
	 * @example "Episode 1. Post-Condition: Post1 AND Post2." --> sentence = "Episode 1", PostConditions = {"Post1", "Post2"} 
	 * @param sentence
	 * @return
	 */
	private static Object extractPostConditions(String sentence, Object returnObject) {
		if(returnObject instanceof StructuredEpisode) {
			//Episodio para retornar unsa sentencia actualizada y sus Post-condiciones
			returnObject = new StructuredEpisode();
			//Inicializar episodio
			((StructuredEpisode) returnObject).createSimpleEpisode(null, sentence);
		} else if (returnObject instanceof StructuredContext) {
			//Contexto para retornar unsa sentencia actualizada y sus Post-condiciones
			returnObject = new StructuredContext();
			//Inicializar episodio
			((StructuredContext) returnObject).createContext(sentence);
		} else if(returnObject instanceof StructuredAlternative) {
			//Alternative/Excecao para retornar una sentencia actualizada y sus Post-condiciones
			returnObject = new StructuredAlternative();
			//Inicializar Alternate/Exception
			((StructuredAlternative) returnObject).createAlternative(null);
			//REMOVE SOLUTION SENTENCES
			((StructuredAlternative) returnObject).setSolution(new ArrayList<String>());
			//ADD SOLUTION
			((StructuredAlternative) returnObject).addSolution(sentence);
		} else {
			return null;
		} 

		// PROCURAR POR: Post-CONDICOES:
		
		Matcher matcherPostCondSentence = RegularExpression.PATTERN_LEFT_SIDE_POST_COND.matcher(sentence);
		int count = 0;
		int startPostCondSentenceLabel = -1;
		int endPostCondSentenceLabel = -1;
		int startPostCondSentenceItems = -1;
		int endPostCondSentenceItems = -1;
		while(matcherPostCondSentence.find()) {
			count++;
			startPostCondSentenceLabel = matcherPostCondSentence.start();
			endPostCondSentenceLabel = matcherPostCondSentence.end();
			break;//firs occurrence
		}

		if (startPostCondSentenceLabel >= 0 && endPostCondSentenceLabel > 0) {
			String sentencePostCondItems = sentence.substring(endPostCondSentenceLabel);
			//Find first occurrence of "\. | \n" (end of PostCond. items)
			
			Matcher matcherEndPostCondItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentencePostCondItems);
			while(matcherEndPostCondItems.find()) {
				count++;
				startPostCondSentenceItems = 0;
				endPostCondSentenceItems = matcherEndPostCondItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE Post-CONDICOES
			if(startPostCondSentenceItems >= 0 && endPostCondSentenceItems > 0) {
				sentencePostCondItems = sentencePostCondItems.substring(startPostCondSentenceItems,endPostCondSentenceItems);
				//ATUALIZAR O EPISODIO COM Post-CONDICOES
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_POST_CONDITION, WHITESPACE_CHAR);
				sentence = sentence.replace(sentencePostCondItems, WHITESPACE_CHAR);
				sentencePostCondItems = sentencePostCondItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);
				String[] items = sentencePostCondItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String condition: items) {
					condition = condition.trim();
					if(returnObject instanceof StructuredEpisode) {
						((StructuredEpisode) returnObject).addPostCondition(condition);
					} else if (returnObject instanceof StructuredContext) {
						((StructuredContext) returnObject).addPostCondition(condition);
					} else if (returnObject instanceof StructuredAlternative) {
						((StructuredAlternative) returnObject).addPostCondition(condition);
					}
				}
				if(returnObject instanceof StructuredEpisode) {
					((StructuredEpisode) returnObject).setSentence(sentence);
				} else if (returnObject instanceof StructuredContext) {
					((StructuredContext) returnObject).setSentence(sentence);
				} else if (returnObject instanceof StructuredAlternative) {
					//REMOVE SOLUTION SENTENCES
					((StructuredAlternative) returnObject).setSolution(new ArrayList<String>());
					//ADD SOLUTION
					((StructuredAlternative) returnObject).addSolution(sentence);

				}

			}
		}
		return returnObject;
	}

	/**
	 * Return a temporal [StructuredEpisode | StructuredContext] with: Constraints and an updated sentence (FREE of Constraints)
	 * @example "Episode 1. Constraints: Post1 AND Post2." --> sentence = "Episode 1", Constraints = {"Post1", "Post2"} 
	 * @param sentence
	 * @return
	 */
	private static Object extractExplicitConstraints(String sentence, Object returnObject) {
		if(returnObject instanceof StructuredEpisode) {
			//Episodio para retornar unsa sentencia actualizada y sus Constraints
			returnObject = new StructuredEpisode();
			//Inicializar episodio
			((StructuredEpisode) returnObject).createSimpleEpisode(null, sentence);
		} else if (returnObject instanceof StructuredContext) {
			//Contexto para retornar unsa sentencia actualizada y sus Constraints
			returnObject = new StructuredContext();
			//Inicializar contexto
			((StructuredContext) returnObject).createContext(sentence);
		} else {
			return null;
		}

		// PROCURAR POR: Constraints:
		
		Matcher matcherConstraintSentence = RegularExpression.PATTERN_LEFT_SIDE_CONSTRAINT.matcher(sentence);
		int count = 0;
		int startConstraintSentenceLabel = -1;
		int endConstraintSentenceLabel = -1;
		int startConstraintSentenceItems = -1;
		int endConstraintSentenceItems = -1;
		while(matcherConstraintSentence.find()) {
			count++;
			startConstraintSentenceLabel = matcherConstraintSentence.start();
			endConstraintSentenceLabel = matcherConstraintSentence.end();
			break;//firs occurrence
		}

		if (startConstraintSentenceLabel >= 0 && endConstraintSentenceLabel > 0) {
			String sentenceConstraintItems = sentence.substring(endConstraintSentenceLabel);
			//Find first occurrence of "\. | \n" (end of Constraint. items)
			
			Matcher matcherEndConstraintItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentenceConstraintItems);
			while(matcherEndConstraintItems.find()) {
				count++;
				startConstraintSentenceItems = 0;
				endConstraintSentenceItems = matcherEndConstraintItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE Constraints
			if(startConstraintSentenceItems >= 0 && endConstraintSentenceItems > 0) {
				sentenceConstraintItems = sentenceConstraintItems.substring(startConstraintSentenceItems,endConstraintSentenceItems);
				//ATUALIZAR O EPISODIO COM Constraints
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_CONSTRAINT, WHITESPACE_CHAR);
				sentence = sentence.replace(sentenceConstraintItems, WHITESPACE_CHAR);
				sentenceConstraintItems = sentenceConstraintItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);

				String[] items = sentenceConstraintItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String constraint: items) {
					constraint = constraint.trim();
					if(returnObject instanceof StructuredEpisode) {
						((StructuredEpisode) returnObject).addConstraint(constraint);
					} else if (returnObject instanceof StructuredContext) {
						//((StructuredContext) returnObject).addConstraint(condition); //FIX: Context has not Constraints yet
					}
				}
				if(returnObject instanceof StructuredEpisode) {
					((StructuredEpisode) returnObject).setSentence(sentence);
				} else if (returnObject instanceof StructuredContext) {
					((StructuredContext) returnObject).setSentence(sentence);
				}

			}
		}
		return returnObject;
	}
	
	/**
	 * Return a temporal [StructuredEpisode | StructuredContext] with: Constraints and an updated sentence (FREE of Constraints)
	 * @example "Episode 1. System queries messages whose date have expired" --> sentence = "System queries messages", Constraints = {"date have expired"} 
	 * @param sentence
	 * @return
	 */
	private static Object extractNonExplicitConstraints(String sentence, Object returnObject) {
		if(returnObject instanceof StructuredEpisode) {
			//Episodio para retornar unsa sentencia actualizada y sus Constraints
			returnObject = new StructuredEpisode();
			//Inicializar episodio
			((StructuredEpisode) returnObject).createSimpleEpisode(null, sentence);
		} else if (returnObject instanceof StructuredContext) {
			//Contexto para retornar unsa sentencia actualizada y sus Constraints
			returnObject = new StructuredContext();
			//Inicializar contexto
			((StructuredContext) returnObject).createContext(sentence);
		} else {
			return null;
		}

		// PROCURAR POR: Constraints:
		
		Matcher matcherConstraintSentence = RegularExpression.PATTERN_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT.matcher(sentence);
		int count = 0;
		int startConstraintSentenceLabel = -1;
		int endConstraintSentenceLabel = -1;
		int startConstraintSentenceItems = -1;
		int endConstraintSentenceItems = -1;
		while(matcherConstraintSentence.find()) {
			count++;
			startConstraintSentenceLabel = matcherConstraintSentence.start();
			endConstraintSentenceLabel = matcherConstraintSentence.end();
			break;//firs occurrence
		}

		if (startConstraintSentenceLabel >= 0 && endConstraintSentenceLabel > 0) {
			String sentenceConstraintItems = sentence.substring(endConstraintSentenceLabel);
			//Find first occurrence of "\. | \n" (end of Constraint. items)
			
			Matcher matcherEndConstraintItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentenceConstraintItems);
			while(matcherEndConstraintItems.find()) {
				count++;
				startConstraintSentenceItems = 0;
				endConstraintSentenceItems = matcherEndConstraintItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE Constraints
			if(startConstraintSentenceItems >= 0 && endConstraintSentenceItems > 0) {
				sentenceConstraintItems = sentenceConstraintItems.substring(startConstraintSentenceItems,endConstraintSentenceItems);
				//ATUALIZAR O EPISODIO COM Constraints
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT, WHITESPACE_CHAR);
				sentence = sentence.replace(sentenceConstraintItems, WHITESPACE_CHAR);
				sentenceConstraintItems = sentenceConstraintItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);

				String[] items = sentenceConstraintItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String constraint: items) {
					constraint = constraint.trim();
					if(returnObject instanceof StructuredEpisode) {
						((StructuredEpisode) returnObject).addConstraint(constraint);
					} else if (returnObject instanceof StructuredContext) {
						//((StructuredContext) returnObject).addConstraint(condition); //FIX: Context has not Constraints yet
					}
				}
				if(returnObject instanceof StructuredEpisode) {
					((StructuredEpisode) returnObject).setSentence(sentence);
				} else if (returnObject instanceof StructuredContext) {
					((StructuredContext) returnObject).setSentence(sentence);
				}

			}
		}
		return returnObject;
	}

	/**
	 * Return a temporal StructuredContext with: Temporal Locations and an updated sentence (FREE of Temporal Locations)
	 * @example "Context 1. Temporal Locations: Post1 AND Post2." --> sentence = "Context 1", Temporal Locations = {"Post1", "Post2"} 
	 * @param sentence
	 * @return
	 */
	private static StructuredContext extractTemporalLocations(String sentence) {
		//Contexto para retornar unsa sentencia actualizada y sus Constraints
		StructuredContext returnObject = new StructuredContext();
		//Inicializar contexto
		returnObject.createContext(sentence);

		// PROCURAR POR: Temporal Locations:
		
		Matcher matcherTemporalLocSentence = RegularExpression.PATTERN_LEFT_SIDE_TEMPORAL_LOC.matcher(sentence);
		int count = 0;
		int startTemporalLocSentenceLabel = -1;
		int endTemporalLocSentenceLabel = -1;
		int startTemporalLocSentenceItems = -1;
		int endTemporalLocSentenceItems = -1;
		while(matcherTemporalLocSentence.find()) {
			count++;
			startTemporalLocSentenceLabel = matcherTemporalLocSentence.start();
			endTemporalLocSentenceLabel = matcherTemporalLocSentence.end();
			break;//firs occurrence
		}

		if (startTemporalLocSentenceLabel >= 0 && endTemporalLocSentenceLabel > 0) {
			String sentenceTemporalLocItems = sentence.substring(endTemporalLocSentenceLabel);

			//Find first occurrence of "\. | \n" (end of TemporalLoc. items)
			
			Matcher matcherEndTemporalLocItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentenceTemporalLocItems);
			while(matcherEndTemporalLocItems.find()) {
				count++;
				startTemporalLocSentenceItems = 0;
				endTemporalLocSentenceItems = matcherEndTemporalLocItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE Temporal Locations
			if(startTemporalLocSentenceItems >= 0 && endTemporalLocSentenceItems > 0) {
				sentenceTemporalLocItems = sentenceTemporalLocItems.substring(startTemporalLocSentenceItems,endTemporalLocSentenceItems);
				//ATUALIZAR O EPISODIO COM Temporal Locations
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_TEMPORAL_LOCATION, WHITESPACE_CHAR);
				sentence = sentence.replace(sentenceTemporalLocItems, WHITESPACE_CHAR);
				sentenceTemporalLocItems = sentenceTemporalLocItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);

				String[] items = sentenceTemporalLocItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String TemporalLoc: items) {
					TemporalLoc = TemporalLoc.trim();
					returnObject.addTemporalLocation(TemporalLoc); 
				}
				returnObject.setSentence(sentence);

			}
		}
		return returnObject;
	}

	/**
	 * Return a temporal StructuredContext with: Geographical Locations and an updated sentence (FREE of Geographical Locations)
	 * @example "Context 1. Geographical Locations: Post1 AND Post2." --> sentence = "Context 1", Geographical Locations = {"Post1", "Post2"} 
	 * @param sentence
	 * @return
	 */
	private static StructuredContext extractGeographicalLocations(String sentence) {
		//Contexto para retornar unsa sentencia actualizada y sus Constraints
		StructuredContext returnObject = new StructuredContext();
		//Inicializar contexto
		returnObject.createContext(sentence);

		// PROCURAR POR: Geographical Locations:
		Matcher matcherGeographicalLocSentence = RegularExpression.PATTERN_LEFT_SIDE_GEOGRAPHICAL_LOC.matcher(sentence);
		int count = 0;
		int startGeographicalLocSentenceLabel = -1;
		int endGeographicalLocSentenceLabel = -1;
		int startGeographicalLocSentenceItems = -1;
		int endGeographicalLocSentenceItems = -1;
		while(matcherGeographicalLocSentence.find()) {
			count++;
			startGeographicalLocSentenceLabel = matcherGeographicalLocSentence.start();
			endGeographicalLocSentenceLabel = matcherGeographicalLocSentence.end();
			break;//firs occurrence
		}

		if (startGeographicalLocSentenceLabel >= 0 && endGeographicalLocSentenceLabel > 0) {
			String sentenceGeographicalLocItems = sentence.substring(endGeographicalLocSentenceLabel);
			//Find first occurrence of "\. | \n" (end of GeographicalLoc. items)
			
			Matcher matcherEndGeographicalLocItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(sentenceGeographicalLocItems);
			while(matcherEndGeographicalLocItems.find()) {
				count++;
				startGeographicalLocSentenceItems = 0;
				endGeographicalLocSentenceItems = matcherEndGeographicalLocItems.end();
				break;//first occurrence
			}
			//CRIAR UMA LISTA DE Geographical Locations
			if(startGeographicalLocSentenceItems >= 0 && endGeographicalLocSentenceItems > 0) {
				sentenceGeographicalLocItems = sentenceGeographicalLocItems.substring(startGeographicalLocSentenceItems,endGeographicalLocSentenceItems);
				//ATUALIZAR O EPISODIO COM Geographical Locations
				sentence = sentence.replaceFirst(RegularExpression.REGEX_LEFT_SIDE_GEOGRAPHICAL_LOCATION, WHITESPACE_CHAR);
				sentence = sentence.replace(sentenceGeographicalLocItems, WHITESPACE_CHAR);
				sentenceGeographicalLocItems = sentenceGeographicalLocItems.replaceAll(RegularExpression.REGEX_END_INDICATOR_ITEMS, EMPTY_CHAR);

				String[] items = sentenceGeographicalLocItems.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String GeographicalLoc: items) {
					GeographicalLoc = GeographicalLoc.trim();
					returnObject.addGeographicalLocation(GeographicalLoc); 
				}
				returnObject.setSentence(sentence);

			}
		}
		return returnObject;
	}
	/**
	 * Return an [Episode | Alternative] Id
	 * @example "1. Episode 1." --> id = "1", sentence = "Episode 1" 
	 * @param sentence
	 * @return
	 */
	private static String extractSentenceId(String sentence) {
		String sentenceId = null;
		// PROCURAR POR: ID:
	
		Matcher matcherIdSentence = RegularExpression.PATTERN_EPISODE_ID.matcher(sentence);

		int count = 0;
		int startIdSentenceLabel = -1;
		int endIdSentenceLabel = -1;

		while(matcherIdSentence.find()) {
			count++;
			startIdSentenceLabel = matcherIdSentence.start();
			endIdSentenceLabel = matcherIdSentence.end();
			break;//firs occurrence
		}
		//SE EPISODIO | EXCECAO TEM ID VÁLIDO ENTAO CRIAR ID
		if (startIdSentenceLabel >= 0 && endIdSentenceLabel > 0) {
			//CREAR ID DE SENTENCA
			sentenceId = sentence.substring(startIdSentenceLabel, endIdSentenceLabel).trim();
			//LIMPAR O ID-EPISODIO DE ", ; . %s" AL FINAL 
			sentenceId = sentenceId.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, "");
			//ATUALIZAR EPISODIO | EXCECAO
			sentence = sentence.replaceFirst(RegularExpression.REGEX_EPISODE_ID, EMPTY_CHAR);

		}
		
		return sentenceId;
	}

	/**
	 * Obtain an Structured Conditional Episode (StructuredEpisode) from Non-structured Episode description: 
	 * IF <condition> THEN <sentence>
	 * @param sentence
	 * @return
	 */
	private static  StructuredEpisode extracExplicitConditionalEpisode (String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;

		// PROCURAR POR: IF
		Matcher matcherStartCondition = RegularExpression.PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_CONDITION.matcher(sentence);
		int count = 0;
		int startStartConditionLabel = -1;
		int endStartConditionLabel = -1;
		int startConditionItems = -1;
		int endConditionItems = -1;
		while(matcherStartCondition.find()) {
			count++;
			startStartConditionLabel = matcherStartCondition.start();
			endStartConditionLabel = matcherStartCondition.end();
			break;//firs occurrence
		}
		//SE episodio CONDITIONAL
		if (startStartConditionLabel >= 0 && endStartConditionLabel > 0) {
			//VERIFICAR SE EXISTE: THEN
			//Find first occurrence of "THEN" (end of conditions)
			
			Matcher matcherStartAction = RegularExpression.PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_ACTION.matcher(sentence);
			while(matcherStartAction.find()) {
				count++;
				startConditionItems = 0;
				endConditionItems = matcherStartAction.start();
				break;//first occurrence
			}
			//SE EXISTE ACTION ENTAO CRIAR EPISODIO CONDICIONAL: ACTION Y UMA LISTA DE CONDICOES: 
			if(startConditionItems >= 0 && endConditionItems > 0) {
				String conditions = sentence.substring(endStartConditionLabel,endConditionItems);
				//Inicializar episodio
				returnObject = new StructuredEpisode();
				returnObject.createConditionalEpisode(null, sentence);

				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_CONDITIONS, EMPTY_CHAR);
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_ACTION, EMPTY_CHAR);
				sentence = sentence.replaceFirst(conditions, EMPTY_CHAR);

				//Incializar Episodio Estructurado
				returnObject.setSentence(sentence);
				//CRIAR UMA LISTA DE CONDICOES: 
				String[] items = conditions.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String condition: items) {
					condition = condition.trim().replace(NON_SEQUENTIAL_GROUP_CHAR, EMPTY_CHAR);
					returnObject.addCondition(condition);
				}
			}

		}
		return returnObject;
	}
	

	
	/**
	 * Obtain an Structured Conditional Episode (StructuredEpisode) from Non-structured Episode description: 
	 * <sentence> (, IF | WHEN) <condition>
	 * @param sentence
	 * @return
	 */
	private static  StructuredEpisode extractNonExplicitConditionalEpisode (String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;

		// PROCURAR POR: IF/WHEN
		Matcher matcherStartCondition = RegularExpression.PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_NON_EXPLICIT_CONDITION.matcher(sentence);
		int count = 0;
		int startStartConditionLabel = -1;
		int endStartConditionLabel = -1;
		int startConditionItems = -1;
		int endConditionItems = -1;
		while(matcherStartCondition.find()) {
			count++;
			startStartConditionLabel = matcherStartCondition.start();
			endStartConditionLabel = matcherStartCondition.end();
			break;//firs occurrence
		}
		//SE episodio CONDITIONAL
		if (startStartConditionLabel >= 0 && endStartConditionLabel > 0) {
			//VERIFICAR SE EXISTE antes de IF: <sentence>
			String sentenceCandidate = sentence.substring(0, startStartConditionLabel).trim();
			if(sentenceCandidate != null && !sentenceCandidate.isEmpty()) {
				//SE EXISTE ACTION ENTAO CRIAR EPISODIO CONDICIONAL: ACTION Y UMA LISTA DE CONDICOES:
				startConditionItems = endStartConditionLabel;
				if(startConditionItems >= 0) {
					String conditions = sentence.substring(endStartConditionLabel);
					//Inicializar episodio
					returnObject = new StructuredEpisode();
					returnObject.createConditionalEpisode(null, sentenceCandidate);

					sentence = sentenceCandidate.replaceFirst(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_NON_EXPLICIT_CONDITIONS, EMPTY_CHAR);
					
					sentence = sentence.replaceFirst(conditions, EMPTY_CHAR);

					//Incializar Episodio Estructurado
					returnObject.setSentence(sentence);
					//CRIAR UMA LISTA DE CONDICOES: 
					String[] items = conditions.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
					for (String condition: items) {
						condition = condition.trim().replace(NON_SEQUENTIAL_GROUP_CHAR, EMPTY_CHAR);
						returnObject.addCondition(condition);
					}
				}
			}			
		}
		return returnObject;
	}
	
	/**
	 * Obtain an Structured Optional Episode (StructuredEpisode) from Non-structured Episode (Scenario.episodes.get(i) description
	 * @param sentence
	 * @return
	 */
	private static  StructuredEpisode extractOptionalEpisode(String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;

		// PROCURAR POR: [
		
		Matcher matcherStartOption = RegularExpression.PATTERN_START_DELIMITING_OPTIONAL_EPISODE.matcher(sentence);
		int count = 0;
		int startStartOptionLabel = -1;
		int endStartOptionLabel = -1;
		int startConditionItems = -1;
		int endConditionItems = -1;
		while(matcherStartOption.find()) {
			count++;
			startStartOptionLabel = matcherStartOption.start();
			endStartOptionLabel = matcherStartOption.end();
			break;//firs occurrence
		}
		//SE episodio OPTIONAL
		if (startStartOptionLabel >= 0 && endStartOptionLabel > 0) {
			//VERIFICAR SE EXISTE: ]
			//Find first occurrence of "]" (end of optional episode)
			Matcher matcherEndOption = RegularExpression.PATTERN_END_DELIMITING_OPTIONAL_EPISODE.matcher(sentence);
			int startEndOptionLabel = -1;
			int endEndOptionLabel = -1;
			while(matcherEndOption.find()) {
				count++;
				startEndOptionLabel = matcherEndOption.start();
				endEndOptionLabel = matcherEndOption.end();
				break;//first occurrence
			}
			//SE EXISTE [...] ENTAO CRIAR EPISODIO OPCIONAL: ACTION Y UMA LISTA DE CONDICOES: 
			if(startEndOptionLabel >= 0 && endEndOptionLabel > 0) {
				//Inicializar episodio
				returnObject = new StructuredEpisode();
				returnObject.createOptionalEpisode(null, sentence);

				//ATUALIZAR O EPISODIO
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_OPTIONAL_EPISODE, EMPTY_CHAR);
				sentence = sentence.replaceFirst(RegularExpression.REGEX_END_DELIMITING_OPTIONAL_EPISODE, EMPTY_CHAR);
				//Incializar Episodio Estructurado
				returnObject.setSentence(sentence);

				//CRIAR UMA LISTA DE CONDICOES: FIX
				String condition = "OK_"+sentence.trim().replace(NON_SEQUENTIAL_GROUP_CHAR, EMPTY_CHAR);
				returnObject.addCondition(condition);

			}

		}
		return returnObject;
	}
	/**
	 * Obtain an Structured ITERATIVE DO-WHILE Episode (StructuredEpisode) from Non-structured Episode (Scenario.episodes.get(i) description
	 * @param sentence
	 * @return
	 */
	private static  StructuredEpisode extractIterativeDoWhileEpisode(String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;
		// PROCURAR POR: DO .. 
		
		Matcher matcherStartLoop = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION.matcher(sentence);
		int count = 0;
		int startStartLoopLabel = -1;
		int endStartLoopLabel = -1;
		int startLoopConditionItems = -1;
		int endLoopConditionItems = -1;
		while(matcherStartLoop.find()) {
			count++;
			startStartLoopLabel = matcherStartLoop.start();
			endStartLoopLabel = matcherStartLoop.end();
			break;//firs occurrence
		}
		//SE episodio LOOP
		if (startStartLoopLabel >= 0 && endStartLoopLabel > 0) {
			//VERIFICAR SE EXISTE: WHILE
			//Find first occurrence of "WHILE" (end of ACTION)
			Matcher matcherStartLoopConditions = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS.matcher(sentence);
			while(matcherStartLoopConditions.find()) {
				count++;
				startLoopConditionItems = matcherStartLoopConditions.end();
				endLoopConditionItems = sentence.length();
				break;//first occurrence
			}
			//SE EXISTE CONDICOES ENTAO CRIAR EPISODIO DO-WHILE: ACTION Y UMA LISTA DE CONDICOES: 
			if(startLoopConditionItems >= 0 && endLoopConditionItems > 0) {
				String conditions = sentence.substring(startLoopConditionItems,endLoopConditionItems);

				//ATUALIZAR O EPISODIO
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION, EMPTY_CHAR);

				//Incializar Episodio Estructurado
				returnObject = new StructuredEpisode();
				returnObject.createIterativeDoWhileEpisode(null, sentence);

				//PROCURAR O FINAL DAS CONDICOES
				//Find first occurrence of "\. | \n" (end of PostCond. loc. items)
				Matcher matcherEndConditionItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(conditions);
				startLoopConditionItems = -1;
				endLoopConditionItems = -1;
				while(matcherEndConditionItems.find()) {
					count++;
					startLoopConditionItems = 0;
					endLoopConditionItems = matcherEndConditionItems.end();
					break;//first occurrence
				}
				//CRIAR UMA LISTA DE CONDICOES
				if(startLoopConditionItems >= 0 && endLoopConditionItems > 0) {
					conditions = conditions.substring(startLoopConditionItems,endLoopConditionItems);
					//CRIAR UMA LISTA DE CONDICOES: 
					String[] items = conditions.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
					for (String condition: items) {
						condition = condition.trim().replace(NON_SEQUENTIAL_GROUP_CHAR, EMPTY_CHAR);
						returnObject.addCondition(condition);

					}
					//ATUALIZAR O EPISODIO
					sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS, EMPTY_CHAR);
					sentence = sentence.replaceFirst(conditions, EMPTY_CHAR);
					//Atualizar setenca
					returnObject.setSentence(sentence);
				}
			}

		}
		return returnObject;
	}
	
	/**
	 * Obtain an Structured ITERATIVE WHILE-DO Episode (StructuredEpisode) from Non-structured Episode (Scenario.episodes.get(i) description
	 * @param sentence
	 * @return
	 */
	private static  StructuredEpisode extractIterativeWhileDoEpisode(String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;
		// PROCURAR POR: WHILE .. 
		Matcher matcherStartLoop = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_CONDITIONS.matcher(sentence);
		int count = 0;
		int startLoopLabel = -1;
		int endLoopLabel = -1;
		int startLoopConditionItems = -1;
		int endLoopConditionItems = -1;
		while(matcherStartLoop.find()) {
			count++;
			startLoopConditionItems = matcherStartLoop.start();
			endLoopConditionItems = matcherStartLoop.end();
			break;//firs occurrence
		}
		//SE episodio LOOP
		if (startLoopConditionItems >= 0 && endLoopConditionItems > 0) {
			//VERIFICAR SE EXISTE: DO
			//Find first occurrence of "DO" (end of CONDITIONS)
			Matcher matcherStartLoopConditions = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION.matcher(sentence);
			while(matcherStartLoopConditions.find()) {
				count++;
				startLoopLabel = matcherStartLoopConditions.end();
				endLoopLabel = sentence.length();
				break;//first occurrence
			}
			//SE EXISTE ACTION ENTAO CRIAR EPISODIO WHILE-DO: ACTION Y UMA LISTA DE CONDICOES: 
			if(startLoopLabel >= 0 && endLoopLabel > 0) {
				String conditions = sentence.substring(startLoopConditionItems,endLoopConditionItems);

				//ATUALIZAR O EPISODIO
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS, EMPTY_CHAR);

				//Incializar Episodio Estructurado
				returnObject = new StructuredEpisode();
				returnObject.createIterativeWhileDoEpisode(null, sentence);

				//PROCURAR O FINAL DAS CONDICOES
				//Find first occurrence of "\. | \n" (end of PostCond. loc. items)
				Matcher matcherEndConditionItems = RegularExpression.PATTERN_END_INDICATOR_LIST_ITEMS.matcher(conditions);
				startLoopConditionItems = -1;
				endLoopConditionItems = -1;
				while(matcherEndConditionItems.find()) {
					count++;
					startLoopConditionItems = 0;
					endLoopConditionItems = matcherEndConditionItems.end();
					break;//first occurrence
				}
				//CRIAR UMA LISTA DE CONDICOES
				if(startLoopConditionItems >= 0 && endLoopConditionItems > 0) {
					conditions = conditions.substring(startLoopConditionItems,endLoopConditionItems);
					//CRIAR UMA LISTA DE CONDICOES: 
					String[] items = conditions.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
					for (String condition: items) {
						condition = condition.trim().replace(NON_SEQUENTIAL_GROUP_CHAR, EMPTY_CHAR);
						returnObject.addCondition(condition);

					}
					//ATUALIZAR O EPISODIO
					sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS, EMPTY_CHAR);
					sentence = sentence.replaceFirst(conditions, EMPTY_CHAR);
					//Atualizar setenca
					returnObject.setSentence(sentence);
				}
			}

		}
		return returnObject;
	}
	
	/**
	 * Obtain an Structured ITERATIVE FOR-EACH-DO Episode (StructuredEpisode) from Non-structured Episode (Scenario.episodes.get(i) description
	 * @param sentence
	 * @return
	 */
	private static StructuredEpisode extractIterativeForEachDoEpisode(String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		StructuredEpisode returnObject = null;
		// PROCURAR POR: FOR-EACH .. 
		Matcher matcherStartLoop = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ITEMS.matcher(sentence);
		int count = 0;
		int startLoopLabel = -1;
		int endLoopLabel = -1;
		int startLoopConditionItems = -1;
		int endLoopConditionItems = -1;
		while(matcherStartLoop.find()) {
			count++;
			startLoopConditionItems = matcherStartLoop.start();
			endLoopConditionItems = matcherStartLoop.end();
			break;//firs occurrence
		}
		//SE episodio LOOP
		if (startLoopConditionItems >= 0 && endLoopConditionItems > 0) {
			//VERIFICAR SE EXISTE: DO
			//Find first occurrence of "DO" (end of CONDITIONS)
			Matcher matcherStartLoopConditions = RegularExpression.PATTERN_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ACTION.matcher(sentence);
			while(matcherStartLoopConditions.find()) {
				count++;
				startLoopLabel = matcherStartLoopConditions.end();
				endLoopLabel = sentence.length();
				break;//first occurrence
			}
			//SE EXISTE ACTION ENTAO CRIAR EPISODIO FOR-EACH: ACTION Y UMA LISTA DE CONDICOES: 
			if(startLoopLabel >= 0 && endLoopLabel > 0) {
				String conditions = sentence.substring(startLoopConditionItems,endLoopConditionItems);//items

				//ATUALIZAR O EPISODIO
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ITEMS, EMPTY_CHAR);

				//Incializar Episodio Estructurado
				returnObject = new StructuredEpisode();
				returnObject.createIterativeForEachDoEpisode(null, sentence);

				
				//CRIAR UMA LISTA DE CONDICOES
				if(!conditions.isEmpty()) {
					returnObject.addCondition(conditions);
					//ATUALIZAR O EPISODIO
					sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS, EMPTY_CHAR);
					sentence = sentence.replaceFirst(conditions, EMPTY_CHAR);
					//Atualizar setenca
					returnObject.setSentence(sentence);
				}
			}

		}
		return returnObject;
	}
	
	/**
	 * Obtain an Structured Alternative (StructuredAlternative) from Non-structured Alternative (Scenario.alternative) description
	 * Alternate/Exception with IF - THEN format
	 * @param sentence
	 * @return
	 */
	private static StructuredAlternative extractCauseAlternativeIfThenFormat(String sentence) {
		//Episodio para retornar un Alternative y sus condicoes
		StructuredAlternative returnObject = null;

		// PROCURAR POR: IF
		Matcher matcherStartCondition = RegularExpression.PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_CONDITION.matcher(sentence);
		int count = 0;
		int startStartConditionLabel = -1;
		int endStartConditionLabel = -1;
		int startConditionItems = -1;
		int endConditionItems = -1;
		while(matcherStartCondition.find()) {
			count++;
			startStartConditionLabel = matcherStartCondition.start();
			endStartConditionLabel = matcherStartCondition.end();
			break;//firs occurrence
		}
		//SE CAUSAS
		if (startStartConditionLabel >= 0 && endStartConditionLabel > 0) {
			//VERIFICAR SE EXISTE: THEN
			//Find first occurrence of "THEN" (end of CAUSAS)
			
			Matcher matcherStartAction = RegularExpression.PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_ACTION.matcher(sentence);
			while(matcherStartAction.find()) {
				count++;
				startConditionItems = 0;
				endConditionItems = matcherStartAction.start();
				break;//first occurrence
			}
			//SE EXISTE SOLUTION ENTAO CRIAR EXCECAO: SOLUTION Y UMA LISTA DE CAUSAS: 
			if(startConditionItems >= 0 && endConditionItems > 0) {
				String causes = sentence.substring(endStartConditionLabel,endConditionItems);
				//LIMPAR a CAUSAS DE ", ; . %s" AL FINAL 
				causes = causes.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
				//Inicializar Alternative
				returnObject = new StructuredAlternative();
				returnObject.createAlternative(null);;
				//ATUALIZAR SENTENCA
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_CONDITIONS, EMPTY_CHAR);
				sentence = sentence.replaceFirst(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_ACTION, EMPTY_CHAR);
				sentence = sentence.replaceFirst(causes, EMPTY_CHAR);
				sentence = sentence.trim();
				//Incializar Alternative Estructurado
				if(!sentence.isEmpty())
					returnObject.addSolution(sentence);
				//CRIAR UMA LISTA DE CAUSAS: 
				String[] items = causes.split(RegularExpression.REGEX_SEPARATOR_CONDITIONS);
				for (String cause: items) {
					cause = cause.trim();
					returnObject.addCause(cause);
				}
			}

		}
		return returnObject;
	}
	
	/**
	 * verify that a sentence does not contain "check IF/WHEN ... " | "see WHETHER ... " structures
	 * @param sentence
	 * @return
	 */
	public static  Boolean isEpisodeWithComplicatedValidationStep (String sentence) {
		//Episodio para retornar un episodio y sus condicoes
		Boolean isValidationStep = false;
		
		// PROCURAR POR: check IF/WHEN | see WHETHER
		sentence = sentence.toLowerCase();
		String newSentence = sentence.replaceFirst(RegularExpression.REG_EX_COMPLICATED_VALIDATION_ACTION_INDICATOR, EMPTY_CHAR);
		if(!newSentence.equals(sentence))
			isValidationStep = true;
		return isValidationStep;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
