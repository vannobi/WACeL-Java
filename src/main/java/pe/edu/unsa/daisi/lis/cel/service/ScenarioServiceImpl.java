package pe.edu.unsa.daisi.lis.cel.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.repository.IScenarioDao;
import pe.edu.unsa.daisi.lis.cel.util.ListManipulation;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.StringManipulation;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioManipulation;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioParser;

@Service("scenarioService")
@Transactional
public class ScenarioServiceImpl implements IScenarioService{

	@Autowired
	private IScenarioDao dao;

	public static final String EMPTY_CHAR = "";
	public static final String WHITESPACE_CHAR = " ";
	public static final String NON_SEQUENTIAL_GROUP_CHAR = "#";
	public static final String NEW_LINE = "\\n";
	public static final String HTML_BREAK = "<br/>";
	
	public static final String PRE_CONDITION_RELATIONSHIP = "PRE_CONDITION";
	public static final String POST_CONDITION_RELATIONSHIP = "POST_CONDITION";
	public static final String CONSTRAINT_RELATIONSHIP = "CONSTRAINT";
	public static final String SUB_SCENARIO_RELATIONSHIP = "SUB_SCENARIO";
	public static final String ALTERNATIVE_RELATIONSHIP = "ALTERNATIVE";
	public static final String EXPLICIT_NON_SEQ_RELATIONSHIP = "EXPLICIT";
	public static final String NON_EXPLICIT_NON_SEQ_RELATIONSHIP = "NON_EXPLICIT";
	
	public Scenario findById(Long id) {
		return dao.findById(id);
	}

	public Scenario findByTitle(String title) {
		Scenario scenario = dao.findByTitle(title);
		return scenario;
	}

	public void saveScenario(Scenario scenario) {
		scenario.setInclusionDate(Calendar.getInstance());
		scenario.setExclusionFlag(false);

		dao.save(scenario);
	}

	/*
	 * Since the method is running with Transaction, No need to call hibernate update explicitly.
	 * Just fetch the entity from db and update it with proper values within transaction.
	 * It will be updated in db once transaction ends. 
	 */
	public void updateScenario(Scenario scenario) {
		Scenario entity = dao.findById(scenario.getId());
		if(entity!=null){
			entity.setTitle(scenario.getTitle());
			entity.setGoal(scenario.getGoal());
			entity.setContext(scenario.getContext());
			entity.setActors(scenario.getActors());
			entity.setResources(scenario.getResources());
			entity.setEpisodes(scenario.getEpisodes());
			entity.setAlternative(scenario.getAlternative());

		}
	}



	public void deleteScenarioById(Long id) {
		//set exclusion flag to true
		Scenario entity = dao.findById(id);
		if(entity!=null){
			entity.setExclusionFlag(true);
		}
		//dao.deleteById(id);
	}

	public List<Scenario> findAllScenarios() {
		return dao.findAllScenarios();
	}

	public List<Scenario> findAllArchivedScenarios() {
		return dao.findAllArchivedScenarios();
	}

	public List<Scenario> findScenariosByProjectId(Long projectId) {
		return dao.findScenariosByProjectId(projectId);
	}

	public boolean isScenarioTitleUnique(Long id, String title) {
		Scenario scenario = findByTitle(title);
		return ( scenario == null || ((id != null) && (scenario.getId() == id)));
	}


	public StructuredScenario convertToStructuredScenario(Scenario scenario) {
		return ScenarioParser.parseToStructuredScenario(scenario);
	}


	public List<StructuredScenario> findSequentiallyRelatedScenariosByPreConditionAndPostCondition(StructuredScenario structuredScenario, List<StructuredScenario> scenarios) {
		List<StructuredScenario> relatedScenarios = new ArrayList<StructuredScenario>();  
		if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
			for(String preCondition : structuredScenario.getContext().getPreConditions()) {
				//BUSCAR POST-CONDICAO NA LISTA DE CENARIOS
				if(scenarios != null && !scenarios.isEmpty()) {
					for(StructuredScenario relatedScenario : scenarios) {
						if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
							for(String postCondition : relatedScenario.getContext().getPostConditions()) {
								if(preCondition.toUpperCase().equals(postCondition.toUpperCase())) {
									relatedScenarios.add(relatedScenario);
									break;
								}
							}
						}
					}
				}
			}
		}
		return relatedScenarios;
	}
	
	
	public HashMap<String, List<StructuredScenario>> findSequentiallyRelatedScenarios(StructuredScenario structuredScenario, List<StructuredScenario> scenarios) {
		
		List<StructuredScenario> relatedScenarios = new ArrayList<StructuredScenario>();  
		List<StructuredScenario> sortedScenarios = new ArrayList<StructuredScenario>();  
		HashMap<String, List<StructuredScenario>> seqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Sequentially related scenarios by type of relationship
		if(scenarios != null && !scenarios.isEmpty()) {
			//Sort scenarios
			sortedScenarios = ListManipulation.copyList(scenarios);
			sortedScenarios = ScenarioManipulation.sortScenarios(sortedScenarios, 0, sortedScenarios.size() - 1) ;
			//Pre-condition: is a relationship defined within the context element of a scenario. A scenario that is pre-condition to other must be executed first.
			if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
				for(String preCondition : structuredScenario.getContext().getPreConditions()) {
					for(StructuredScenario relatedScenario : sortedScenarios) {
						if(preCondition.toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
							if (ScenarioManipulation.isSEquentiallyRelated(structuredScenario, preCondition, relatedScenario)) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(PRE_CONDITION_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(PRE_CONDITION_RELATIONSHIP, tmpScenarios);
								
								break;
							}			
							
							/*
							String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
							Pattern patternTitle = Pattern.compile(regExpTitle);
							String context = preCondition.toUpperCase();
							Matcher matcherTitle = patternTitle.matcher(context);
							int startIndex = -1;
							int endIndex = -1;
							while(matcherTitle.find()) {
								startIndex = matcherTitle.start();
								endIndex = matcherTitle.end();
								break;//firs occurrence
							}
							if (startIndex >= 0 && endIndex > 0) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(PRE_CONDITION_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(PRE_CONDITION_RELATIONSHIP, tmpScenarios);
								
								break;
							}							
							*/
						}
						
					}
				}
			}
			//Post-condition is a relationship defined within the context element of a scenario. A scenario that is post-condition of other must be executed last.
			if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
				for(String postCondition : structuredScenario.getContext().getPostConditions()) {
					for(StructuredScenario relatedScenario : sortedScenarios) {
						if(postCondition.toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
							if (ScenarioManipulation.isSEquentiallyRelated(structuredScenario, postCondition, relatedScenario)) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(POST_CONDITION_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(POST_CONDITION_RELATIONSHIP, tmpScenarios);
								
								break;
							}		
							/*
							String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
							Pattern patternTitle = Pattern.compile(regExpTitle);
							String context = postCondition.toUpperCase();
							Matcher matcherTitle = patternTitle.matcher(context);
							int startIndex = -1;
							int endIndex = -1;
							while(matcherTitle.find()) {
								startIndex = matcherTitle.start();
								endIndex = matcherTitle.end();
								break;//firs occurrence
							}
							if (startIndex >= 0 && endIndex > 0) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(POST_CONDITION_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(POST_CONDITION_RELATIONSHIP, tmpScenarios);
								break;
							}
							*/							
						}					
					}
				}
			}
			//Sub-scenario is defined when an episode of a scenario can be described by another scenario. This allows the decomposition of complex scenarios.
			if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {
				for(StructuredEpisode episode : structuredScenario.getEpisodes()) {
					String sentence = episode.getSentence().toUpperCase();//.toUpperCase();
					for(StructuredScenario relatedScenario : sortedScenarios) {			
						if(sentence.contains(relatedScenario.getTitle().toUpperCase())) {
							if (ScenarioManipulation.isSEquentiallyRelated(structuredScenario, sentence, relatedScenario)) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(SUB_SCENARIO_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(SUB_SCENARIO_RELATIONSHIP, tmpScenarios);
								
								break;
							}
							/*
							String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
							Pattern patternTitle = Pattern.compile(regExpTitle);
							Matcher matcherTitle = patternTitle.matcher(sentence);
							int startIndex = -1;
							int endIndex = -1;
							while(matcherTitle.find()) {
								startIndex = matcherTitle.start();
								endIndex = matcherTitle.end();
								break;//firs occurrence
							}
							if (startIndex >= 0 && endIndex > 0) {
								List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(SUB_SCENARIO_RELATIONSHIP);
								if(tmpScenarios == null)
									tmpScenarios = new ArrayList<StructuredScenario>();  
								if(!tmpScenarios.contains(relatedScenario))
									tmpScenarios.add(relatedScenario);
								seqRelatedScenarioHashMap.put(SUB_SCENARIO_RELATIONSHIP, tmpScenarios);
								break;
							}	
							*/						
						}						
					}
				}
			}
			//Alternative relationship is defined when a scenario is used to handle an Alternative of a scenario. The Alternative scenario is executed when the alternative is triggered in the main scenario.
			if(structuredScenario.getAlternative() != null && !structuredScenario.getAlternative().isEmpty()) {
				for(StructuredAlternative alternative : structuredScenario.getAlternative()) {
					if(alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
						for(String solution : alternative.getSolution()) {
							for(StructuredScenario relatedScenario : sortedScenarios) {
								if(solution.toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
									if (ScenarioManipulation.isSEquentiallyRelated(structuredScenario, solution, relatedScenario)) {
										List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(ALTERNATIVE_RELATIONSHIP);
										if(tmpScenarios == null)
											tmpScenarios = new ArrayList<StructuredScenario>();  
										if(!tmpScenarios.contains(relatedScenario))
											tmpScenarios.add(relatedScenario);
										seqRelatedScenarioHashMap.put(ALTERNATIVE_RELATIONSHIP, tmpScenarios);
										
										break;
									}
									/*
									String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
									Pattern patternTitle = Pattern.compile(regExpTitle);
									solution = solution.toUpperCase();
									Matcher matcherTitle = patternTitle.matcher(solution);
									int startIndex = -1;
									int endIndex = -1;
									while(matcherTitle.find()) {
										startIndex = matcherTitle.start();
										endIndex = matcherTitle.end();
										break;//firs occurrence
									}
									if (startIndex >= 0 && endIndex > 0) {
										List<StructuredScenario> tmpScenarios = seqRelatedScenarioHashMap.get(ALTERNATIVE_RELATIONSHIP);
										if(tmpScenarios == null)
											tmpScenarios = new ArrayList<StructuredScenario>();  
										if(!tmpScenarios.contains(relatedScenario))
											tmpScenarios.add(relatedScenario);
										seqRelatedScenarioHashMap.put(ALTERNATIVE_RELATIONSHIP, tmpScenarios);
										break;
									}			
									*/
									
								}
							}							
						}
					}
					
				}
			}
			//Constraint relationship is defined when a scenario is used to detail non-functional aspects that qualify/restrict the proper execution of another. It gives us an order among the scenarios.
		}
		
		//return relatedScenarios;
		return seqRelatedScenarioHashMap;
	}
	
	
	public HashMap<String, List<StructuredScenario>> findNonSequentiallyRelatedScenarios(StructuredScenario structuredScenario, List<StructuredScenario> scenarios, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenarios) {
		List<StructuredScenario> explicitNonSeqRelatedScenarios = new ArrayList<StructuredScenario>(); //#<epsodes>#  
		List<StructuredScenario> nonExplicitNonSeqRelatedScenarios = new ArrayList<StructuredScenario>();//pre and post-condition relationships
		HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap = new HashMap<String, List<StructuredScenario>>(); //Non-Sequentially related scenarios by type of relationship (EXPLICIT and NON_EXPLICIT)
		List<StructuredScenario> sortedScenarios = new ArrayList<StructuredScenario>();  
		if(scenarios != null && !scenarios.isEmpty()) {
			//Sort scenarios
			sortedScenarios = ListManipulation.copyList(scenarios);
			sortedScenarios = ScenarioManipulation.sortScenarios(sortedScenarios, 0, sortedScenarios.size() - 1) ;
			//Remove main scenario
			sortedScenarios.remove(structuredScenario);

			//FIND NON-EXPLICIT AND NON-SEQUENTIALLY RELATED SCENARIOS
			for(StructuredScenario relatedScenario : sortedScenarios) {
				if(!relatedScenario.getId().equals(structuredScenario.getId())) {
					//Calculate Proximity Index
					if (ScenarioManipulation.getProximityIndex(structuredScenario, relatedScenario) >= 0.5) {
						boolean existNonSeqRelation = false;
						//NON-DETERMINISM: When a set of pre-conditions described in S(i) appears like pre-conditions in S(j), then, S(i) and S(j) might interact concurrently.
						if(!existNonSeqRelation) {
							//pre-condition (Si) vs pre-condition (Sj)
							if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
								for(String preCondition : structuredScenario.getContext().getPreConditions()) {
									//Context pre-condition (Si) vs Context pre-condition (Sj)
									if(relatedScenario.getContext().getPreConditions() != null && !relatedScenario.getContext().getPreConditions().isEmpty()) {
										if(relatedScenario.getContext().getPreConditions().contains(preCondition)) {
											nonExplicitNonSeqRelatedScenarios.add(relatedScenario);
											existNonSeqRelation = true;
											break;
										}
									}
									//Context pre-condition (Si) vs Episodes pre-condition (Sj)
									if(relatedScenario.getEpisodes() != null && !relatedScenario.getEpisodes().isEmpty()) {
										for(StructuredEpisode episode : relatedScenario.getEpisodes()) {
											if(episode.getPreConditions() != null && !episode.getPreConditions().isEmpty()) {
												if(episode.getPreConditions().contains(preCondition)) {
													nonExplicitNonSeqRelatedScenarios.add(relatedScenario);
													existNonSeqRelation = true;
													break;
												}
											}
										}
									}
									if(existNonSeqRelation)
										break;

								}
							}
							//Episodes pre-condition
							if(!existNonSeqRelation) {
								if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {
									for(StructuredEpisode episode : structuredScenario.getEpisodes()) {
										if(episode.getPreConditions() != null && !episode.getPreConditions().isEmpty()) {
											//Episodes pre-condition (Si) vs Context pre-condition (Sj)
											if(relatedScenario.getContext().getPreConditions() != null && !relatedScenario.getContext().getPreConditions().isEmpty()) {
												for(String preCondition : relatedScenario.getContext().getPreConditions()) {
													if(episode.getPreConditions().contains(preCondition)) {
														nonExplicitNonSeqRelatedScenarios.add(relatedScenario);
														existNonSeqRelation = true;
														break;
													}
												}
											}
											//Episodes pre-condition (Si) vs Episodes pre-condition (Sj)
											for(String preCondition : episode.getPreConditions()) {
												if(relatedScenario.getEpisodes() != null && !relatedScenario.getEpisodes().isEmpty()) {
													for(StructuredEpisode otherEpisode : relatedScenario.getEpisodes()) {
														if(otherEpisode.getPreConditions() != null && !otherEpisode.getPreConditions().isEmpty()) {
															if(otherEpisode.getPreConditions().contains(preCondition)) {
																nonExplicitNonSeqRelatedScenarios.add(relatedScenario);
																existNonSeqRelation = true;
																break;
															}
														}
													}
												}
												if(existNonSeqRelation)
													break;
											}
											if(existNonSeqRelation)
												break;

										}
									}						

								}
							}
						}
						//SYNCHRONIZATION: When a pre-condition described in S(i) appears like post-condition in another scenario S(j), and a pre-condition described in S(j) appears like post-condition in S(i), then, S(i) and S(j) might interact concurrently
						if(!existNonSeqRelation) {
							//pre-condition (Si) vs post-condition (Sj)
							boolean preCondSiPostCondSj = false;
							boolean preCondSjPostCondSi = false;
							if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
								for(String preCondition : structuredScenario.getContext().getPreConditions()) {
									//Context pre-condition (Si) vs Context post-condition (Sj)
									if(relatedScenario.getContext().getPostConditions() != null && !relatedScenario.getContext().getPostConditions().isEmpty()) {
										if(relatedScenario.getContext().getPostConditions().contains(preCondition)) {
											preCondSiPostCondSj = true;
											break;
										}
									}
									//Context pre-condition (Si) vs Episodes post-condition (Sj)
									if(relatedScenario.getEpisodes() != null && !relatedScenario.getEpisodes().isEmpty()) {
										for(StructuredEpisode episode : relatedScenario.getEpisodes()) {
											if(episode.getPostConditions() != null && !episode.getPostConditions().isEmpty())
												if(episode.getPostConditions().contains(preCondition)) {
													preCondSiPostCondSj = true;
													break;
												}										
										}
									}								
									if(preCondSiPostCondSj)
										break;
								}
							}
							if(!preCondSiPostCondSj) {
								if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {								
									for(StructuredEpisode episode : structuredScenario.getEpisodes()) {
										if(episode.getPreConditions() != null && !episode.getPreConditions().isEmpty()) {
											for(String preCondition : episode.getPreConditions()) {
												//Episodes pre-condition (Si) vs Context post-condition (Sj)
												if(relatedScenario.getContext().getPostConditions() != null && !relatedScenario.getContext().getPostConditions().isEmpty()) {
													if(relatedScenario.getContext().getPostConditions().contains(preCondition)) {
														preCondSiPostCondSj = true;
														break;
													}
												}
												//Episodes pre-condition (Si) vs Episodes post-condition (Sj)
												if(relatedScenario.getEpisodes() != null && !relatedScenario.getEpisodes().isEmpty()) {
													for(StructuredEpisode otherEpisode : relatedScenario.getEpisodes()) {
														if(otherEpisode.getPostConditions() != null && !otherEpisode.getPostConditions().isEmpty())
															if(otherEpisode.getPostConditions().contains(preCondition)) {
																preCondSiPostCondSj = true;
																break;
															}										
													}
												}								
												if(preCondSiPostCondSj)
													break;
											}
										}
										if(preCondSiPostCondSj)
											break;
									}
								}
							}

							//pre-condition (Sj) vs post-condition (Si)
							if(relatedScenario.getContext().getPreConditions() != null && !relatedScenario.getContext().getPreConditions().isEmpty()) {
								for(String preCondition : relatedScenario.getContext().getPreConditions()) {
									//Context pre-condition (Sj) vs Context post-condition (Si)
									if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
										if(structuredScenario.getContext().getPostConditions().contains(preCondition)) {
											preCondSjPostCondSi = true;
											break;
										}
									}
									//Context pre-condition (Sj) vs Episodes post-condition (Si)
									if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {
										for(StructuredEpisode episode : structuredScenario.getEpisodes()) {
											if(episode.getPostConditions() != null && !episode.getPostConditions().isEmpty())
												if(episode.getPostConditions().contains(preCondition)) {
													preCondSjPostCondSi = true;
													break;
												}										
										}
									}								
									if(preCondSjPostCondSi)
										break;
								}
							}
							if(!preCondSjPostCondSi) {
								if(relatedScenario.getEpisodes() != null && !relatedScenario.getEpisodes().isEmpty()) {								
									for(StructuredEpisode episode : relatedScenario.getEpisodes()) {
										if(episode.getPreConditions() != null && !episode.getPreConditions().isEmpty()) {
											for(String preCondition : episode.getPreConditions()) {
												//Episodes pre-condition (Sj) vs Context post-condition (Si)
												if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
													if(structuredScenario.getContext().getPostConditions().contains(preCondition)) {
														preCondSjPostCondSi = true;
														break;
													}
												}
												//Episodes pre-condition (Sj) vs Episodes post-condition (Si)
												if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {
													for(StructuredEpisode otherEpisode : structuredScenario.getEpisodes()) {
														if(otherEpisode.getPostConditions() != null && !otherEpisode.getPostConditions().isEmpty())
															if(otherEpisode.getPostConditions().contains(preCondition)) {
																preCondSjPostCondSi = true;
																break;
															}										
													}
												}								
												if(preCondSjPostCondSi)
													break;
											}
										}
										if(preCondSjPostCondSi)
											break;
									}
								}
							}

							if(preCondSiPostCondSj && preCondSjPostCondSi) {
								nonExplicitNonSeqRelatedScenarios.add(relatedScenario);
								existNonSeqRelation = true;
								break;
							}						

						}					
					}
				}
			}
			
			
			List<Integer> removeNonSeqScenarios = new ArrayList<>();//Remove by Index
			if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
				//FILTER SET OF NON-EXPLICIT AND NON-SEQUENTIALLY SCENARIOS BY REMOVING SEQUENTIALLY RELATED SCENARIOS OF NON-EXPLICIT AND NON-SEQUENTIALLY SCENARIOS SET: SUB-SCENARIO, CONSTRAINT, ALTERNATIVE, PRE-CONDITION,POST-CONDITION
				if(nonExplicitNonSeqRelatedScenarios != null && !nonExplicitNonSeqRelatedScenarios.isEmpty()) {
					for(int index = 0 ; index < nonExplicitNonSeqRelatedScenarios.size(); index++) {
						StructuredScenario nonSeqScenario = nonExplicitNonSeqRelatedScenarios.get(index);
						boolean removed = false;
						//Remove CONSTRAINT
						//TBD
		
						//Remove PRE-CONDITION
						List<StructuredScenario> preScenarios = sequentiallyRelatedScenarios.get(PRE_CONDITION_RELATIONSHIP);
						if(preScenarios != null) {
							for(StructuredScenario seqScenario : preScenarios) {
								if(nonSeqScenario.getId().equals(seqScenario.getId())) {
									removeNonSeqScenarios.add(index);
									removed = true;
									break;
								}
							}
						}
						//Remove POST-CONDITION
						if(!removed) {
							List<StructuredScenario> postScenarios = sequentiallyRelatedScenarios.get(POST_CONDITION_RELATIONSHIP);
							if(postScenarios != null) {
								for(StructuredScenario seqScenario : postScenarios) {
									if(nonSeqScenario.getId().equals(seqScenario.getId())) {
										removed = true;
										removeNonSeqScenarios.add(index);
										break;
									}
								}
							}
						}
						//Remove ALTERNATIVE
						if(!removed) {
							List<StructuredScenario> exceptScenarios = sequentiallyRelatedScenarios.get(ALTERNATIVE_RELATIONSHIP);
							if(exceptScenarios != null) {
								for(StructuredScenario seqScenario : exceptScenarios) {
									if(nonSeqScenario.getId().equals(seqScenario.getId())) {
										removed = true;
										removeNonSeqScenarios.add(index);
										break;
									}
								}
							}
						}
						//Remove SUB-SCENARIO
						if(!removed) {
							List<StructuredScenario> subScenarios = sequentiallyRelatedScenarios.get(SUB_SCENARIO_RELATIONSHIP);
							if(subScenarios != null) {
								for(StructuredScenario seqScenario : subScenarios) {
									if(nonSeqScenario.getId().equals(seqScenario.getId())) {
										removed = true;
										removeNonSeqScenarios.add(index);
										break;
									}
								}
							}
						}
						
						
					}
					//Remove
					for(int index : removeNonSeqScenarios) {
						nonExplicitNonSeqRelatedScenarios.remove(index);
					}					
				}	
				
				
				//CREATE SET OF EXPLICIT AND NON-SEQUENTIALLY SCENARIOS BY INCLUDING SEQUENTIALLY RELATED SCENARIOS (SUB-SCENARIO) TO EXPLICIT AND NON-SEQUENTIALLY SCENARIOS SET
				//SUB-SCENARIO  into non-sequential episodes (#<episodes series>#) group
				List<StructuredScenario> subScenarios = sequentiallyRelatedScenarios.get(SUB_SCENARIO_RELATIONSHIP);
				if(subScenarios != null) {
					for(StructuredScenario seqScenario : subScenarios) {

						//Check that SEQ Scenario appears into a NON-SEQ GROUP 
						if(structuredScenario.getEpisodes() !=  null && !structuredScenario.getEpisodes().isEmpty()) {
							int startNonSequentialGroup = -1;
							int endNonSequentialGroup = -1;
							for(int indexEpisode = 0; indexEpisode < structuredScenario.getEpisodes().size(); indexEpisode++) {
								StructuredEpisode episode = structuredScenario.getEpisodes().get(indexEpisode);
								if(episode.getSentence() != null && episode.getSentence().startsWith(NON_SEQUENTIAL_GROUP_CHAR)) 
									startNonSequentialGroup = indexEpisode;
								if(episode.getSentence() != null && episode.getSentence().endsWith(NON_SEQUENTIAL_GROUP_CHAR))
									endNonSequentialGroup = indexEpisode;
								//Search between #<episodes series>#
								if(startNonSequentialGroup >= 0 && endNonSequentialGroup > startNonSequentialGroup) {
									for(int j = startNonSequentialGroup; j <= endNonSequentialGroup; j++) {
										StructuredEpisode innerEpisode = structuredScenario.getEpisodes().get(j);
										if(innerEpisode.getSentence().toUpperCase().contains(seqScenario.getTitle().toUpperCase())) {

											//Add explicit
											explicitNonSeqRelatedScenarios = nonSeqRelatedScenarioHashMap.get(EXPLICIT_NON_SEQ_RELATIONSHIP);
											if(explicitNonSeqRelatedScenarios == null)
												explicitNonSeqRelatedScenarios = new ArrayList<StructuredScenario>();  
											if(!explicitNonSeqRelatedScenarios.contains(seqScenario))
												explicitNonSeqRelatedScenarios.add(seqScenario);
											nonSeqRelatedScenarioHashMap.put(EXPLICIT_NON_SEQ_RELATIONSHIP, explicitNonSeqRelatedScenarios);

											break;
										}	
									}													
								}	

							}
						}																			

					}
				}
				
			}	
			//CREATE SET OF NON-EXPLICIT AND NON-SEQUENTIALLY SCENARIOS 
			nonSeqRelatedScenarioHashMap.put(NON_EXPLICIT_NON_SEQ_RELATIONSHIP, nonExplicitNonSeqRelatedScenarios);
			
		}
		
		return nonSeqRelatedScenarioHashMap;
	}
	

	

	}
