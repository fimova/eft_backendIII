package com.duoc.batch_service.controller;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired(required = false)
    @Qualifier("transaccionJob")
    private Job transaccionJob;

    @Autowired(required = false)
    @Qualifier("cuentaInteresJob")
    private Job cuentaInteresJob;

    @Autowired(required = false)
    @Qualifier("movimientoAnualJob")
    private Job movimientoAnualJob;

    @PostMapping("/transacciones")
    public ResponseEntity<?> ejecutarTransacciones() {
        if (transaccionJob == null) {
            return ResponseEntity.badRequest().body("Job transacciones no disponible");
        }
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            var execution = jobLauncher.run(transaccionJob, jobParameters);
            return ResponseEntity.ok("Job transacciones iniciado. ID: " + execution.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/intereses")
    public ResponseEntity<?> ejecutarIntereses() {
        if (cuentaInteresJob == null) {
            return ResponseEntity.badRequest().body("Job intereses no disponible");
        }
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            var execution = jobLauncher.run(cuentaInteresJob, jobParameters);
            return ResponseEntity.ok("Job intereses iniciado. ID: " + execution.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/movimientos")
    public ResponseEntity<?> ejecutarMovimientos() {
        if (movimientoAnualJob == null) {
            return ResponseEntity.badRequest().body("Job movimientos no disponible");
        }
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            var execution = jobLauncher.run(movimientoAnualJob, jobParameters);
            return ResponseEntity.ok("Job movimientos iniciado. ID: " + execution.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/todos")
    public ResponseEntity<?> ejecutarTodos() {
        try {
            StringBuilder resultado = new StringBuilder();
            
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            if (transaccionJob != null) {
                var exec1 = jobLauncher.run(transaccionJob, params);
                resultado.append("Transacciones: ").append(exec1.getId()).append(" | ");
            }
            
            if (cuentaInteresJob != null) {
                var exec2 = jobLauncher.run(cuentaInteresJob, params);
                resultado.append("Intereses: ").append(exec2.getId()).append(" | ");
            }
            
            if (movimientoAnualJob != null) {
                var exec3 = jobLauncher.run(movimientoAnualJob, params);
                resultado.append("Movimientos: ").append(exec3.getId());
            }
            
            return ResponseEntity.ok(resultado.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("Batch Service está corriendo");
    }
}