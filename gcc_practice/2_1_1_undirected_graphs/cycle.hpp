#pragma once

// cycle.hpp

#include <vector>  // for inner bipartite structures
#include "graph.h" // for graph api

/**
 *  Find cycle in a given undirected graph G = (V, E)
 */
class Cycle
{
private:
  std::vector<int> cycle;
  std::vector<int> edge_to;
  std::vector<bool> marked;

  /**
   * Run recursive version of depth-first search algorithm to find a cycle
   * @param G given undirected graph
   * @param v the source vertex
   */
  void dfs_recursive (const graph &G, const int v)
  {
    marked[v] = true;
    for (auto w : G.adj (v))
      {
        // algorithm should stop if cycle has been already found
        if (cycle.size () > 0)
          return;

        if (!marked[w]) //< if adjacent w is unmarked, run dfs
          {
            edge_to[w] = v;
            dfs_recursive (G, w);
          }
        else if (w != edge_to[v]) //< if adjacent w is marked, but dfs(G, v) didn`t start from w, we have found a cycle!
          {
            for (int x = v; x != w; x = edge_to[x])
              {
                cycle.push_back (x);
              }
            cycle.push_back (w);
          }
      }
  }

  /**
   * @brief Check if a given graph G has a self loop
   *
   * @param G a given graph
   * @return true if G has a self loop, false otherwise
   */
  bool has_self_loop (const graph &G)
  {
    for (int v = 0; v < G.get_vertexex_num (); v++)
      {
        for (auto w : G.adj (v))
          {
            if (v == w)
              {
                cycle.push_back (v);
                cycle.push_back (w);
                return true;
              }
          }
      }
    return false;
  }

  /**
   * @brief Check if a given graph G has parallel edges
   *
   * @param G a given graph
   * @return true if G has parallel edges, false otherwise
   */
  bool has_parallel_edges (const graph &G)
  {
    for (int v = 0; v < G.get_vertexex_num (); v++)
      {
        for (auto w : G.adj (v))
          {
            if (marked[w])
              {
                cycle.push_back (v);
                cycle.push_back (w);
                cycle.push_back (v);
                return true;
              }
            marked[w] = true;
          }
        for (auto w : G.adj (v))
          marked[w] = false;
      }
    return false;
  }

public:
  Cycle () = delete;
  
  // constructor
  Cycle (const graph &G) : marked (G.get_vertexex_num (), false), edge_to (G.get_vertexex_num (), -1)
  {
    if (has_self_loop (G) || has_parallel_edges (G))
      return;
    for (int v = 0; v < G.get_vertexex_num (); v++)
      {
        if (cycle.size () == 0 && !marked[v])
          {
            dfs_recursive (G, v);
          }
      }
  }


  // check if graph has a cycle
  bool has_cycle () const { return (cycle.size () > 0); }

  // get a graph cycle
  std::vector<int> & get_cycle () { return cycle; }
};

