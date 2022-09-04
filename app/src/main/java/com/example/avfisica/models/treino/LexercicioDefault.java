package com.example.avfisica.models.treino;

import java.util.ArrayList;
import java.util.List;



public class LexercicioDefault {

    private static final int QTD_EXERCICIO = 70;
    private List<Exercicio> lExercicio = new ArrayList<Exercicio>() ;

    //Construtor
    public LexercicioDefault()
    {
        boolean flag;
        for (int indice=1; indice<=QTD_EXERCICIO; indice++)
        {
            Exercicio exercicio = new Exercicio();
            flag = true;
            switch (indice)
            {
                case 1:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remada com Halteres com um braço");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("exercicio_remadas_com_halteres_com_um_braco");
                break;
                case 2:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Supino (peito, tricips)");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("exercicio_supino_peito_tricips");
                    break;
                case 3:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Agachamento barra");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("agachamento_barra");
                    break;
                case 4:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Biceps com halter unilateral");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("biceps_com_halter_unilateral");
                    break;
                case 5:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Biceps maquina");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("biceps_maquina");
                    break;
                case 6:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Crossover");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("crossover");
                    break;
                case 7:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Crucifixo invertido com halter");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("crucifixo_invertido_com_halter");
                    break;
                case 8:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Crucifixo invertido sentado");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("crucifixo_invertido_sentado");
                    break;
                case 9:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Deselvovimento sentando com halter");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("deselvovimento_sentando_com_halter");
                    break;
                case 10:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Desenvolvimento com halter em pe");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("desenvolvimento_com_halter_pe");
                    break;
                case 11:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Desenvolvimento máquina");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("desenvolvimento_maquina");
                    break;
                case 12:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Eleveção frontal");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("elevacao_frontal");
                    break;
                case 13:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Flexao braco");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("flexao_braco");
                    break;
                case 14:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Mesa flexora");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("mesa_flexora");
                    break;
                case 15:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Puxador triangulo");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("puxador_triangulo");
                    break;
                case 16:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remada curvada");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("remada_curvada");
                    break;
                case 17:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remada fechada com halter");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("remada_fechada_com_halter");
                    break;
                case 18:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Rosca scott halter");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("rosca_scott_halter");
                    break;
                case 19:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Stiff barra");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("stiff_barra");
                    break;
                case 20:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Supino reto com barra");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("supino_reto");
                    break;
                case 21:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Antebraço barra");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("antebraco_barra");
                    break;
                case 22:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Voador maquina");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("voador_maquina");
                    break;
                case 23:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Supino halter");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("supino_halter");
                    break;
                case 24:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Elevacao frontal barra");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("elevacao_frontal_barra");
                    break;
                case 25:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Crucifixo halter");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("crucifixo_halter");
                    break;
                case 26:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Puxador frente");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("puxador_frente");
                    break;
                case 27:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Afundo");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("afundo");
                    break;
                case 28:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Leg press 45g");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("leg_press_45g");
                    break;
                case 29:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Agachamento");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("agachamento");
                    break;
                case 30:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Flexao braco inclinado");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("flexao_braco_inclinado");
                    break;
                case 31:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps frances");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_frances");
                    break;
                case 32:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Supino inclinado halter");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("supino_inclina_halter");
                    break;
                case 33 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps testa halter");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_testa_halter");
                    break;
                case 34 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remada alta com barra");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("remada_alta_com_barra");
                    break;
                case 35 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Crucifixo invertido inclinado");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("crucifixo_invertido_inclinado");
                    break;
                case 36 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Pullover");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("pullover");
                    break;
                case 37 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Rosca scott halter");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("rosca_scott_halter");
                    break;
                case 38 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Deselvovimento sentando com halter");
                    exercicio.setTipo("ombro");
                    exercicio.setDrawable("deselvovimento_sentando_com_halter");
                    break;
                case 39 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Roda abdominal");
                    exercicio.setTipo("abdominal");
                    exercicio.setDrawable("roda_abdominal");
                    break;
                case 40 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Roda W");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("roda_w");
                    break;
                case 41 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps rosca w");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_rosca_w");
                    break;
                case 42 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Agachamento peso");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("agachamento_peso");
                    break;
                case 43 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps coice");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_coice");
                    break;
                case 44 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps frances unilateral");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_fraces_unilateral");
                    break;
                case 45 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Puxador");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("puxador_");
                    break;
                case 46 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps corda");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_corda");
                    break;
                case 47 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remada barra");
                    exercicio.setTipo("costa");
                    exercicio.setDrawable("remada_barra");
                    break;
                case 48 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Corda marinheiro");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("corda_marinheiro");
                    break;
                case 49 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Remo");
                    exercicio.setTipo("aerobico");
                    exercicio.setDrawable("remo");
                    break;
                case 50 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Corrida esteira");
                    exercicio.setTipo("aerobico");
                    exercicio.setDrawable("corrida_esteira");
                    break;
                case 51 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Eliptico");
                    exercicio.setTipo("aerobico");
                    exercicio.setDrawable("eliptico");
                    break;
                case 52 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Triceps banco");
                    exercicio.setTipo("braço");
                    exercicio.setDrawable("triceps_banco");
                    break;
                case 53 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Abdominal barra");
                    exercicio.setTipo("abdominal");
                    exercicio.setDrawable("abdominal_barra");
                    break;
                case 54 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Abdominal crunch");
                    exercicio.setTipo("abdominal");
                    exercicio.setDrawable("abdominal_crunch");
                    break;
                case 55 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Agachamento no smith");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("agachamento_smith");
                    break;
                case 56 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Supino inclinado com barra");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("supino_inclinado_com_barra");
                    break;
                case 57 :
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Leg press 45g unilateral");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("leg_press_45g_unilateral");
                    break;
                case 58:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Panturrilha no leg press");
                    exercicio.setTipo("perna");
                    exercicio.setDrawable("panturrilha_no_leg_press");
                    break;
                case 59:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Bike");
                    exercicio.setTipo("aerobico");
                    exercicio.setDrawable("bicicleta_ergometrica");
                    break;
                case 60:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Fly inclinado com halteres");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("fly_inclinado_com_halteres");
                    break;
                case 61:
                    exercicio.setId_excercicio(indice);
                    exercicio.setNome("Peck deck aberto");
                    exercicio.setTipo("peito");
                    exercicio.setDrawable("peck_deck_aberto");
                    break;


                default:
                    flag = false;
                    break;
            }
            if (flag) {
                exercicio.setPath_gif("-");
                exercicio.setStatus("OK");
                lExercicio.add(exercicio);
            }
            else
                break;
        }
    }


    public List<Exercicio> getlExercicio() {
        return lExercicio;
    }

    public void setlExercicio(List<Exercicio> lExercicio) {
        this.lExercicio = lExercicio;
    }
}


