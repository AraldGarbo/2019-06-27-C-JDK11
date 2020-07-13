package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	List<String> reati;
	List<Giornata> giornate;
	List<String> vertici;
	Graph<String, DefaultWeightedEdge> grafo;
	List<Integer> pesi;
	List<Legame> legami;
	
	
	public Model() {
		this.dao = new EventsDao();
	}
	
	public List<String> getReati(){
		this.reati = this.dao.getReati();
		return this.reati;
	}
	
	public List<Giornata> getDate(){
		this.giornate = this.dao.getDate();
		return this.giornate;
	}

	public void creaGrafo(String reato, Giornata giorno) {
	 this.vertici = this.dao.getVertici(reato, giorno);
	 this.pesi = new ArrayList<Integer>();
	 System.out.println("vertici: " + vertici.size());
	 this.grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	 Graphs.addAllVertices(this.grafo, this.vertici);
	 System.out.println("vertexset: "+this.grafo.vertexSet().size());
	 
	//aggiungo gli archi
		for(String f1 : this.vertici) {
			for(String f2 : this.vertici) {
				if(f1.compareTo(f2) > 0) { //non sono uguali e non li prende doppi
					Integer peso = dao.getPeso(f1, f2); //chiamo il metodo dal dao per il peso e lo salvo nella variabile Double 
								if(peso > 0) { //controllo //se vale zero non lo considero
	 								if(this.grafo.getEdge(f1, f2) == null) { //se non contiene l'arco
									Graphs.addEdge(this.grafo, f1, f2, peso); //aggiungi arco
									System.out.println(peso);
									this.pesi.add(peso);
									}	
								}
							}
						}
					}
		System.out.println("\nnumero di archi: " + this.grafo.edgeSet().size()); //debug
		

	}
	
	public Integer getMax() {
		Integer max = 0;
		for(Integer i : this.pesi) {
			if(i > max) {
				max = i;
			}
		}
		return max;
	}
	
	public Integer getMin() {
		Integer min = this.getMax();
		for(Integer i : this.pesi) {
			if(i < min) {
				min = i;
			}
		}
		return min;
	}
	
	public Double getMedia() {
		Double media = (double) ((this.getMax() + this.getMin())/2); 
		System.out.println(media);
		return media;
	}
	
	public List<Legame> getLegami(){
		this.legami = new ArrayList<Legame>();
		for(String f1 : this.vertici) {
			for(String f2 : this.vertici) {
				if(f1.compareTo(f2) > 0) {
					if(this.grafo.getEdgeWeight(this.grafo.getEdge(f1, f2)) < this.getMedia()) {
						this.legami.add(new Legame(f1, f2, this.grafo.getEdgeWeight(this.grafo.getEdge(f1, f2))));
					}
				}
			}
		}
		System.out.println("risultato size: "+legami.size());
		return this.legami;
	}
	
	
	
}
