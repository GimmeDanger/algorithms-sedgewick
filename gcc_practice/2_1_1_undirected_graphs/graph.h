#pragma once

// undirected_graph.h

#include <vector>

class graph
{
private:
  int vertexes_num = 0;
  int edges_num = 0;
  std::vector<std::vector<int> > adjacency_list;

public:
  graph (unsigned int m_vertexes_num)
    {
      vertexes_num = m_vertexes_num;
      edges_num = 0;
      adjacency_list.resize (vertexes_num);
    }

  int get_vertexex_num () const { return vertexes_num; }
  int get_edges_num () const { return edges_num; }

  void add_vertex ()
  {
    vertexes_num++;
    adjacency_list.push_back (std::vector<int> ());
  }

  void add_edge (int v, int w)
    {
      edges_num++;
      adjacency_list[v].push_back (w);
      adjacency_list[w].push_back (v);
    }

  const std::vector<int> & adj (int v) const
    {
      return adjacency_list[v];
    }

  int degree (int v) const
    {
      int deg = 0;
      for (auto w : adj (v)) deg++;
      return deg;
    }
};