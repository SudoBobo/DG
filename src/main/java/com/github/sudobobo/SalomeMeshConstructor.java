package com.github.sudobobo;

import com.github.sudobobo.IO.MeshFileReader;
import com.github.sudobobo.basis.Basis;
import com.github.sudobobo.geometry.Point;

import java.nio.file.Path;
import java.util.*;

public class SalomeMeshConstructor {
    public SalomeMeshConstructor(Path meshFile) {
        // (с трай кэтчем)
        // потоково вычитывай

        Point[] points = MeshFileReader.readPoints(meshFile);
        removeDublicates(points);


        // ltrb if needed
        // как угодно сначала, можно и в промежуточный объект буфер

        //create points

        Point[] ltrb = findBorders(ps);
        //create triangles & set domain
        Triangle[] t = new Triangle[fileData.Triangles];
        Set<Integer> domains = new HashSet();

        for (int tIndex = 0; tIndex < fileData.triangles.length; tIndex++) {
            int[] tPoints = new int[3];
            for (int pIndex = 0; pIndex < tPoints.length; pIndex++) {
                tPoints[pIndex] = ps[fileData.triangles[tIndex][pIndex]].getId();
            }
            //check orientation is reverse clock (left)
            Vector a = new Vector(ps[tPoints[0]], ps[tPoints[1]]);
            Vector b = new Vector(ps[tPoints[0]], ps[tPoints[2]]);
            if (a.mult2D(b) < 0) {//cменить порядок точек
                int tmp = tPoints[1];
                tPoints[1] = tPoints[2];
                tPoints[2] = tmp;
            }
            t[tIndex] = new Triangle(tPoints, ps, config.defaultBorder);
            // последний элемент отвечает за домен
            // сначала сохраняем в треугольники домен просто как цифру из .mesh файла
            t[tIndex].domain = fileData.triangles[tIndex][fileData.triangles[tIndex].length - 1];
            domains.add(t[tIndex].domain);
        }
        //domain to index
        Integer[] domainsUnique = domains.toArray(new Integer[domains.size()]);
        Arrays.sort(domainsUnique);
        // а тут заполняем в треугольниках поле "домен" 0,1,2 (индексами листа доменов)
        // то есть были домены {1,33,100}
        // а стали {0,1,2}
        List<Integer> domainsList = new ArrayList(Arrays.asList(domainsUnique));
        for (int tIndex = 0; tIndex < fileData.triangles.length; tIndex++) {
            t[tIndex].domain = domainsList.indexOf(t[tIndex].domain);
            if (t[tIndex].domain < 0) {
                throw new AderException("domain identification problem!");
            }
        }
        //find neighbours
        for (int triangle = 0; triangle < t.length; triangle++) {
            for (int edge = 0; edge < t[triangle].points.length; edge++) {
                if (t[triangle].neighbours[edge] != config.defaultBorder) {
                    continue;
                }
                int p0 = t[triangle].points[edge];
                int p1 = t[triangle].points[(edge + 1) % t[triangle].points.length];

                // TODO 'neigbour' actualy means 'potential neigbour'
                for (int neighbour = 0; neighbour < t.length; neighbour++) {

                    if (triangle == neighbour) {
                        continue;
                    }

                    // для каждого соседа проверяем все его рёбра на предмет совпадения с нашими рёбрами
                    for (int nEdge = 0; nEdge < t[neighbour].points.length; nEdge++) {

                        // если у текущего треугольника-потенциального соседа
                        // уже есть сосед на этом ребре, то пропускаем
                        if (t[neighbour].neighbours[nEdge] != config.defaultBorder) {
                            continue;
                        }

                        // получение начальной и конечной точек ребра соседа
                        int np0 = t[neighbour].points[nEdge];
                        int np1 = t[neighbour].points[(nEdge + 1) % t[neighbour].points.length];

                        // само сравнение
                        if (p0 == np1 && p1 == np0) {//include left orientation
                            t[triangle].neighbours[edge] = neighbour;
                            t[triangle].neighbourEdges[edge] = nEdge;

                            // если он мне сосед, то и я ему сосед
                            t[neighbour].neighbours[nEdge] = triangle;
                            t[neighbour].neighbourEdges[nEdge] = edge;
                        }
                    }
                }

                // если граничные треугольники не определены, то этот треугольник на границе сетки

                //still not detected ? -> set borders from xml
                if (t[triangle].neighbours[edge] == config.defaultBorder && !config.borders.isEmpty()) {
                    //by points detect edge index
                    // проходим по всем имеющимся граничным рёбрам
                    for (int globalEdge = 0; globalEdge < fileData.Edges; globalEdge++) {
                        // pi - massive representation of border edge
                        // pi[0] and pi[1] point of border edge
                        int[] pi = fileData.edges[globalEdge];
                        if ((pi[0] == p0 && pi[1] == p1) || (pi[1] == p0 && pi[0] == p1)) {
                            // сопоставили ребро граничному ребру
                            // всем граничным рёбрам поставлен в соответствие какой-то тип
                            // так как расчёт идёт по каждому ребру, то и "включаться/выключаться"
                            // они должны независимо
                            // Иными словами - не треугольник граничный, а ребро граничное
                            //by edge index find mapping to border type
                            Integer type = config.borders.get(pi[pi.length - 1]);
                            if (type != null) {
                                t[triangle].neighbours[edge] = type;
                            }
                            break;
                        }
                    }
                }
            }
        }
        Mesh mesh = new Mesh(ps, t, ltrb);
        return mesh;
    }

    public static Mesh constructHomoMesh(double lambda, double mu, double rho, double spatialStep, double spatialStepForNumericalIntegration, Basis basis) {
        return null;
    }
}
